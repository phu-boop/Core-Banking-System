# Local Environment Setup

Copy the root example or a service-specific example to an ignored local file:

```bash
cp .env.example .env
cp services/identity-service/.env.example services/identity-service/.env
```

Before starting a service, load its environment file:

```bash
set -a
source services/identity-service/.env
set +a
./mvnw -f services/identity-service/pom.xml spring-boot:run
```

Replace every `replace-with-*` value with a local credential. Never commit a `.env` file or a secret value.
