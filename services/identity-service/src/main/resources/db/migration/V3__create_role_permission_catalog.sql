CREATE TABLE identity_schema.permission_catalog (
    permission_code TEXT PRIMARY KEY CHECK (length(permission_code) > 0),
    status VARCHAR(24) NOT NULL
        CHECK (status IN ('ACTIVE', 'PENDING_IMPLEMENTATION', 'RESTRICTED')),
    description TEXT NOT NULL CHECK (length(description) > 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE identity_schema.role_catalog (
    role_code VARCHAR(64) PRIMARY KEY CHECK (length(role_code) > 0),
    display_name VARCHAR(120) NOT NULL CHECK (length(display_name) > 0),
    category VARCHAR(32) NOT NULL CHECK (length(category) > 0),
    description TEXT NOT NULL CHECK (length(description) > 0),
    status VARCHAR(8) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE identity_schema.role_version (
    role_version_id UUID PRIMARY KEY,
    role_code VARCHAR(64) NOT NULL
        REFERENCES identity_schema.role_catalog (role_code),
    version INTEGER NOT NULL CHECK (version > 0),
    status VARCHAR(9) NOT NULL CHECK (status IN ('DRAFT', 'PUBLISHED')),
    created_by TEXT NOT NULL CHECK (length(created_by) > 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_by TEXT,
    published_at TIMESTAMPTZ,
    CONSTRAINT role_version_role_version_unique UNIQUE (role_code, version),
    CONSTRAINT role_version_publication_fields_check CHECK (
        (status = 'DRAFT' AND published_by IS NULL AND published_at IS NULL)
        OR
        (status = 'PUBLISHED'
            AND published_by IS NOT NULL AND length(published_by) > 0
            AND published_at IS NOT NULL)
    )
);

CREATE TABLE identity_schema.role_version_permission (
    role_version_id UUID NOT NULL
        REFERENCES identity_schema.role_version (role_version_id),
    permission_code TEXT NOT NULL
        REFERENCES identity_schema.permission_catalog (permission_code),
    PRIMARY KEY (role_version_id, permission_code)
);

CREATE TABLE identity_schema.role_assignment (
    assignment_id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES identity_schema.user_identity (user_id),
    role_version_id UUID NOT NULL
        REFERENCES identity_schema.role_version (role_version_id),
    scope_type VARCHAR(12) NOT NULL
        CHECK (scope_type IN ('SELF', 'ORGANIZATION', 'BRANCH')),
    scope_id TEXT NOT NULL CHECK (length(scope_id) > 0),
    status VARCHAR(8) NOT NULL CHECK (status IN ('ACTIVE', 'REVOKED')),
    assigned_by TEXT NOT NULL CHECK (length(assigned_by) > 0),
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valid_from TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMPTZ,
    revoked_by TEXT,
    revoked_at TIMESTAMPTZ,
    revoke_reason TEXT,
    CONSTRAINT role_assignment_expiry_check
        CHECK (expires_at IS NULL OR expires_at > valid_from),
    CONSTRAINT role_assignment_revocation_check CHECK (
        (status = 'ACTIVE' AND revoked_by IS NULL AND revoked_at IS NULL)
        OR
        (status = 'REVOKED' AND revoked_at IS NOT NULL)
    )
);

CREATE INDEX role_assignment_user_status_validity_idx
    ON identity_schema.role_assignment (user_id, status, valid_from, expires_at);

CREATE INDEX role_version_permission_permission_idx
    ON identity_schema.role_version_permission (permission_code, role_version_id);

CREATE FUNCTION identity_schema.prevent_published_role_version_mutation()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
        IF OLD.status = 'PUBLISHED' THEN
            RAISE EXCEPTION 'Published role versions are immutable';
        END IF;
        RETURN OLD;
    END IF;

    IF OLD.status = 'PUBLISHED' THEN
        RAISE EXCEPTION 'Published role versions are immutable';
    END IF;

    IF NEW.role_version_id <> OLD.role_version_id
            OR NEW.role_code <> OLD.role_code
            OR NEW.version <> OLD.version THEN
        RAISE EXCEPTION 'Role version identity cannot be changed';
    END IF;

    IF NEW.status <> OLD.status
            AND NOT (OLD.status = 'DRAFT' AND NEW.status = 'PUBLISHED') THEN
        RAISE EXCEPTION 'A role version can only transition from DRAFT to PUBLISHED';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER role_version_immutable
    BEFORE UPDATE OR DELETE ON identity_schema.role_version
    FOR EACH ROW
    EXECUTE FUNCTION identity_schema.prevent_published_role_version_mutation();

CREATE FUNCTION identity_schema.prevent_published_role_permission_mutation()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    target_role_version_id UUID;
    target_status VARCHAR(9);
BEGIN
    IF TG_OP = 'DELETE' THEN
        target_role_version_id := OLD.role_version_id;
    ELSE
        target_role_version_id := NEW.role_version_id;
    END IF;

    SELECT status
    INTO target_status
    FROM identity_schema.role_version
    WHERE role_version_id = target_role_version_id;

    IF target_status = 'PUBLISHED' THEN
        RAISE EXCEPTION 'Permissions of a published role version are immutable';
    END IF;

    IF TG_OP = 'UPDATE' AND OLD.role_version_id <> NEW.role_version_id THEN
        SELECT status
        INTO target_status
        FROM identity_schema.role_version
        WHERE role_version_id = OLD.role_version_id;

        IF target_status = 'PUBLISHED' THEN
            RAISE EXCEPTION 'Permissions of a published role version are immutable';
        END IF;
    END IF;

    IF TG_OP = 'DELETE' THEN
        RETURN OLD;
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER role_version_permission_immutable
    BEFORE INSERT OR UPDATE OR DELETE ON identity_schema.role_version_permission
    FOR EACH ROW
    EXECUTE FUNCTION identity_schema.prevent_published_role_permission_mutation();

CREATE FUNCTION identity_schema.require_published_role_assignment_version()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
    target_version_status VARCHAR(9);
    target_role_status VARCHAR(8);
BEGIN
    SELECT role_version.status, role_catalog.status
    INTO target_version_status, target_role_status
    FROM identity_schema.role_version
    JOIN identity_schema.role_catalog USING (role_code)
    WHERE role_version_id = NEW.role_version_id;

    IF target_version_status IS DISTINCT FROM 'PUBLISHED'
            OR target_role_status IS DISTINCT FROM 'ACTIVE' THEN
        RAISE EXCEPTION 'Role assignments require a published version of an active role';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER role_assignment_published_version
    BEFORE INSERT OR UPDATE OF role_version_id ON identity_schema.role_assignment
    FOR EACH ROW
    EXECUTE FUNCTION identity_schema.require_published_role_assignment_version();

GRANT SELECT ON identity_schema.permission_catalog,
    identity_schema.role_catalog,
    identity_schema.role_version,
    identity_schema.role_version_permission,
    identity_schema.role_assignment
    TO identity_service_app;
