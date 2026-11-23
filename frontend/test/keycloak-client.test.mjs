import assert from "node:assert/strict";
import { readFile } from "node:fs/promises";
import test from "node:test";

const realmUrl = new URL("../../deploy/local/keycloak/realm-core-banking.json", import.meta.url);
const realm = JSON.parse(await readFile(realmUrl, "utf8"));

test("web client uses authorization code with PKCE and an exact local callback", () => {
  const client = realm.clients.find((item) => item.clientId === "core-banking-web");

  assert.ok(client);
  assert.equal(client.publicClient, true);
  assert.equal(client.protocol, "openid-connect");
  assert.equal(client.standardFlowEnabled, true);
  assert.equal(client.directAccessGrantsEnabled, false);
  assert.equal(client.attributes["pkce.code.challenge.method"], "S256");
  assert.deepEqual(client.redirectUris, ["http://localhost:3000/api/auth/callback/keycloak"]);
  assert.deepEqual(client.webOrigins, ["http://localhost:3000"]);
});

test("web client access tokens include the Identity and Account API audiences", () => {
  const client = realm.clients.find((item) => item.clientId === "core-banking-web");
  const audienceMappers = client?.protocolMappers.filter(
    (mapper) => mapper.protocolMapper === "oidc-audience-mapper",
  );

  assert.deepEqual(
    audienceMappers?.map((mapper) => mapper.config["included.client.audience"]).sort(),
    ["account-service", "identity-service"],
  );
  assert.ok(audienceMappers?.every((mapper) => mapper.config["access.token.claim"] === "true"));
  assert.ok(audienceMappers?.every((mapper) => mapper.config["id.token.claim"] === "false"));
});
