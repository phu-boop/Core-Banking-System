export type ScopedGrant = {
  permission: string;
  scopeType: "SELF" | "ORGANIZATION" | "BRANCH";
  scopeId: string | null;
};

export type IdentityPrincipal = {
  userId: string;
  partyId: string | null;
  grants: ScopedGrant[];
};

export function parseIdentityPrincipal(value: unknown): IdentityPrincipal | null;
