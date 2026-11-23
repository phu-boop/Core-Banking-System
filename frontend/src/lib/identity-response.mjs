const uuidPattern = /^[0-9a-f]{8}(?:-[0-9a-f]{4}){3}-[0-9a-f]{12}$/i;
const scopeTypes = new Set(["SELF", "ORGANIZATION", "BRANCH"]);

function isRecord(value) {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}

function isGrant(value) {
  return (
    isRecord(value) &&
    typeof value.permission === "string" &&
    value.permission.length > 0 &&
    scopeTypes.has(value.scopeType) &&
    (value.scopeId === null || typeof value.scopeId === "string")
  );
}

export function parseIdentityPrincipal(value) {
  if (
    !isRecord(value) ||
    typeof value.userId !== "string" ||
    !uuidPattern.test(value.userId) ||
    !(value.partyId === null || (typeof value.partyId === "string" && uuidPattern.test(value.partyId))) ||
    !Array.isArray(value.grants) ||
    !value.grants.every(isGrant)
  ) {
    return null;
  }

  return {
    userId: value.userId,
    partyId: value.partyId,
    grants: value.grants,
  };
}
