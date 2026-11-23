import { parseIdentityPrincipal } from "./identity-response.mjs";

export async function requestIdentityPrincipal(url, accessToken, request = fetch) {
  let response;
  try {
    response = await request(url, {
      method: "GET",
      headers: {
        Accept: "application/json",
        Authorization: `Bearer ${accessToken}`,
      },
      cache: "no-store",
      redirect: "error",
      signal: AbortSignal.timeout(5000),
    });
  } catch (error) {
    if (
      error instanceof TypeError ||
      (error instanceof DOMException && error.name === "TimeoutError")
    ) {
      return { status: "unavailable" };
    }
    throw error;
  }

  if (response.status === 401) {
    return { status: "unauthorized" };
  }
  if (response.status === 403) {
    return { status: "forbidden" };
  }
  if (!response.ok) {
    return { status: "unavailable" };
  }

  let payload;
  try {
    payload = await response.json();
  } catch (error) {
    if (error instanceof SyntaxError) {
      return { status: "invalid-response" };
    }
    throw error;
  }

  const principal = parseIdentityPrincipal(payload);
  return principal ? { status: "authenticated", principal } : { status: "invalid-response" };
}
