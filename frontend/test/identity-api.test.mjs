import assert from "node:assert/strict";
import test from "node:test";
import { requestIdentityPrincipal } from "../src/lib/identity-api-core.mjs";

const principal = {
  userId: "6f0f8b5a-8a5f-4dcb-ae59-fb0063f5b132",
  partyId: null,
  grants: [{ permission: "branch:read", scopeType: "BRANCH", scopeId: "branch-1" }],
};

function response(status, body) {
  return {
    status,
    ok: status >= 200 && status < 300,
    json: async () => body,
  };
}

test("sends the bearer token and accepts a valid Identity principal", async () => {
  let requestUrl;
  let requestOptions;
  const result = await requestIdentityPrincipal(
    "https://identity.example/v1/identity/me",
    "test-access-token",
    async (url, options) => {
      requestUrl = url;
      requestOptions = options;
      return response(200, principal);
    },
  );

  assert.equal(requestUrl, "https://identity.example/v1/identity/me");
  assert.equal(requestOptions.headers.Authorization, "Bearer test-access-token");
  assert.deepEqual(result, { status: "authenticated", principal });
});

test("distinguishes an inactive or unmapped identity from an expired token", async () => {
  const forbidden = await requestIdentityPrincipal(
    "https://identity.example/v1/identity/me",
    "token",
    async () => response(403),
  );
  const unauthorized = await requestIdentityPrincipal(
    "https://identity.example/v1/identity/me",
    "token",
    async () => response(401),
  );

  assert.deepEqual(forbidden, { status: "forbidden" });
  assert.deepEqual(unauthorized, { status: "unauthorized" });
});

test("rejects malformed principal payloads instead of treating them as authenticated", async () => {
  const result = await requestIdentityPrincipal(
    "https://identity.example/v1/identity/me",
    "token",
    async () => response(200, { userId: "not-a-uuid", partyId: null, grants: [] }),
  );

  assert.deepEqual(result, { status: "invalid-response" });
});
