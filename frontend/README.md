# Core Banking frontend

Next.js + TypeScript operations workspace with Keycloak sign-in and a server-side connection to Identity Service. Account, transaction, customer and approval data are not yet integrated.

## Run locally

```bash
cd frontend
npm install
cp .env.example .env.local
openssl rand -base64 32
# Put the generated value in AUTH_SECRET in .env.local
npm run dev
```

Start local Keycloak and Identity Service first. The frontend client is `core-banking-web`; its local callback URL is `http://localhost:3000/api/auth/callback/keycloak`. The Keycloak realm import configures a public OIDC client with Standard Flow, PKCE (S256), no password grant, and `identity-service` plus `account-service` audiences in access tokens. If the realm already exists, create/update the client in Keycloak Admin Console: turn Client authentication off, enable Standard Flow, require S256 PKCE, set the exact redirect URI and web origin above, then add Audience mappers for both services to access tokens only. Realm import does not update an existing realm.

Identity Service uses `GET /v1/identity/me` to resolve the authenticated Keycloak `(issuer, subject)` to an active, pre-provisioned Core Banking identity. Keycloak login can therefore succeed while this API returns `403` for an unmapped or disabled user; the dashboard reports that state and does not invent a principal. This frontend does not provision users or grants.

Open <http://localhost:3000> and sign in through Keycloak. The OAuth access token remains in Auth.js's encrypted, HTTP-only server session and is used only for the server-to-server Identity API request. `AUTH_KEYCLOAK_ISSUER` and `IDENTITY_API_BASE_URL` must use HTTPS in production; HTTP is accepted only for local loopback development.

The overview's account, transaction and approval summaries remain unavailable until their APIs are selected and integrated. Other navigation items are intentionally unavailable until their screens and API contracts are implemented.

## Validate

```bash
npm test
npm run lint
npm run build
```

The UI tokens are based on `../skills/core-banking-frontend/references/design-system.md`.
