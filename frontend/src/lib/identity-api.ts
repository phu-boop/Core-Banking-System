import "server-only";
import { requestIdentityPrincipal, type IdentityLookup } from "./identity-api-core.mjs";
import { validateSecureUrl } from "./secure-url.mjs";
export type { IdentityLookup };

function identityEndpoint(): string | null {
  const configuredBaseUrl = process.env.IDENTITY_API_BASE_URL;
  if (!configuredBaseUrl) {
    return null;
  }

  const baseUrl = validateSecureUrl(
    configuredBaseUrl,
    "IDENTITY_API_BASE_URL",
    process.env.NODE_ENV === "production",
  );

  baseUrl.pathname = `${baseUrl.pathname.replace(/\/+$/, "")}/v1/identity/me`;
  return baseUrl.toString();
}

export function loadCurrentPrincipal(accessToken: string): Promise<IdentityLookup> {
  const endpoint = identityEndpoint();
  if (!endpoint) {
    return Promise.resolve({ status: "configuration-missing" });
  }
  return requestIdentityPrincipal(endpoint, accessToken);
}
