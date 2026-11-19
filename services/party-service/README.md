# Party Service

Party/Customer master-data service for Phase 2.

## Scope

- `POST /v1/customers`
- `GET /v1/customers/{partyId}`
- `PATCH /v1/customers/{partyId}`
- `party_schema` owns Party, IndividualProfile, durable audit and idempotency records.
- Individual profile stores a National ID hash, not raw National ID.
- Duplicate detection is not automatic: soft-match fields and human review remain approval work.
- Create idempotency uses actor, `PARTY_CREATE`, `CUSTOMER`, key and a 24-hour retention.
- PATCH uses optimistic `version` and rejects stale updates.

## Configuration

| Environment variable | Purpose |
|---|---|
| `KEYCLOAK_ISSUER_URI` | Exact expected OIDC issuer |
| `KEYCLOAK_JWK_SET_URI` | HTTPS JWKS URI |
| `PARTY_TOKEN_AUDIENCE` | Required access-token audience |
| `PARTY_DATASOURCE_URL` | PostgreSQL JDBC URL |
| `PARTY_DATASOURCE_USERNAME` | `party_service_app` runtime role |
| `PARTY_DATASOURCE_PASSWORD` | Runtime role secret |

## Database

Bootstrap and migrations are explicit and separated from application startup:

```sh
PARTY_SERVICE_MIGRATOR_PASSWORD=... PARTY_SERVICE_APP_PASSWORD=... \
psql -f services/party-service/database/bootstrap/party-service-ownership.sql

./mvnw -f services/party-service/pom.xml flyway:migrate
```

The runtime role can read/write party domain data but cannot access Flyway history or other schemas.

## Build and test

```sh
./mvnw -f services/party-service/pom.xml verify
```
