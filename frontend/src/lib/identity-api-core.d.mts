import type { IdentityPrincipal } from "./identity-response.mjs";

export type IdentityLookup =
  | { status: "authenticated"; principal: IdentityPrincipal }
  | { status: "unauthorized" }
  | { status: "forbidden" }
  | { status: "configuration-missing" }
  | { status: "unavailable" }
  | { status: "invalid-response" };

export function requestIdentityPrincipal(
  url: string,
  accessToken: string,
  request?: typeof fetch,
): Promise<IdentityLookup>;
