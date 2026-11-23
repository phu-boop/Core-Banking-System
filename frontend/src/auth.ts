import NextAuth from "next-auth";
import Keycloak from "next-auth/providers/keycloak";
import { validateSecureUrl } from "@/lib/secure-url.mjs";

const issuer = process.env.AUTH_KEYCLOAK_ISSUER;
const isProduction = process.env.NODE_ENV === "production";

if (issuer) {
  validateSecureUrl(issuer, "AUTH_KEYCLOAK_ISSUER", isProduction);
}
if (process.env.AUTH_URL) {
  validateSecureUrl(process.env.AUTH_URL, "AUTH_URL", isProduction);
}

export const { handlers, auth, signIn, signOut } = NextAuth({
  useSecureCookies: process.env.NODE_ENV === "production",
  providers: [
    Keycloak({
      clientId: process.env.AUTH_KEYCLOAK_ID ?? "core-banking-web",
      clientSecret: "",
      issuer,
      authorization: {
        params: {
          scope: "openid profile email",
        },
      },
      checks: ["pkce", "state", "nonce"],
      client: {
        token_endpoint_auth_method: "none",
      },
    }),
  ],
  session: {
    strategy: "jwt",
    maxAge: 60 * 60,
  },
  pages: {
    signIn: "/login",
  },
  callbacks: {
    async jwt({ token, account }) {
      if (account) {
        if (!account.access_token) {
          throw new TypeError("Keycloak did not return an access token.");
        }
        token.accessToken = account.access_token;
      }
      return token;
    },
  },
});

declare module "next-auth/jwt" {
  interface JWT {
    accessToken?: string;
  }
}
