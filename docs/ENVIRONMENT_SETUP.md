# Local Environment Setup

Copy the root example or a service-specific example to an ignored local file:

```bash
cp .env.example .env
cp services/identity-service/.env.example services/identity-service/.env
```

Spring Boot automatically imports a service's `.env` when running the Maven plugin from the repository root or that service's directory. The service-specific `.env` takes precedence over the root `.env`; operating-system environment variables and command-line properties take precedence over both. You do not need to `source` the file.

```bash
./mvnw -f services/identity-service/pom.xml spring-boot:run -Dspring-boot.run.profiles=local
```

Use the matching module path for another service, for example:

```bash
./mvnw -f services/party-service/pom.xml spring-boot:run -Dspring-boot.run.profiles=local
./mvnw -f services/ledger-service/pom.xml spring-boot:run -Dspring-boot.run.profiles=local
```

Replace every `replace-with-*` value with a local credential. Never commit a `.env` file or a secret value.
The `local` profile allows HTTP only for OIDC issuer/JWKS URLs. Without an explicit profile, HTTPS is required; use the `production` profile in production deployments.

## Docker Compose + Minikube

Use [deploy/local/docker-compose.yml](../deploy/local/docker-compose.yml) for PostgreSQL and Keycloak:

```bash
cp deploy/local/.env.example deploy/local/.env
docker compose --env-file deploy/local/.env -f deploy/local/docker-compose.yml up -d
docker compose --env-file deploy/local/.env -f deploy/local/docker-compose.yml ps
```

Or let the local setup script generate ignored passwords and prepare Minikube:

```bash
bash deploy/local/setup-minikube-local.sh
```

Then start Minikube and deploy the Kubernetes overlay:

```bash
minikube start
kubectl apply -k deploy/kubernetes/local
```

PostgreSQL and Keycloak remain host processes behind Docker Compose. Minikube reaches them through `postgres` and `keycloak` Kubernetes ExternalName services.
