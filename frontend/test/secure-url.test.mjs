import assert from "node:assert/strict";
import test from "node:test";
import { validateSecureUrl } from "../src/lib/secure-url.mjs";

test("allows local HTTP only for loopback development", () => {
  assert.equal(
    validateSecureUrl(
      "http://localhost:8180/realms/core-banking",
      "AUTH_KEYCLOAK_ISSUER",
      false,
    ).protocol,
    "http:",
  );
  assert.throws(
    () => validateSecureUrl("http://localhost:8180/realms/core-banking", "AUTH_KEYCLOAK_ISSUER", true),
    /must be HTTPS/,
  );
  assert.throws(
    () => validateSecureUrl("http://keycloak.example/realms/core-banking", "AUTH_KEYCLOAK_ISSUER", false),
    /must be HTTPS/,
  );
});

test("rejects credentials and query strings in configured service URLs", () => {
  assert.throws(
    () => validateSecureUrl("https://user:password@identity.example", "IDENTITY_API_BASE_URL", true),
    /must be HTTPS/,
  );
  assert.throws(
    () => validateSecureUrl("https://identity.example?token=secret", "IDENTITY_API_BASE_URL", true),
    /must be HTTPS/,
  );
});
