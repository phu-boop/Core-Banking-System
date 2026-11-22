# API Documentation

## Local development

Build and start the centralized documentation service:

```bash
./mvnw -f services/api-docs-service/pom.xml package
java -jar services/api-docs-service/target/api-docs-service.jar
```

Open:

```text
http://localhost:8090/swagger-ui/index.html
```

The service dropdown loads specifications through same-origin proxy routes:

```text
/specs/identity
/specs/party
/specs/branch
/specs/corporate
/specs/account
/specs/ledger
/specs/transfer
/specs/payment
/specs/audit
```

Each business service exposes its own OpenAPI JSON at `/v3/api-docs`.

## Authenticate in Swagger UI

For services that use OAuth2/JWT:

1. Select the service in the Swagger dropdown.
2. Click **Authorize**.
3. Enter a valid bearer token as `Bearer <token>`.
4. Call a protected endpoint.

Account and Ledger do not currently expose a JWT security scheme because their source security configuration is not implemented.

## Kubernetes local

Build the docs image first:

```bash
./mvnw -f services/api-docs-service/pom.xml package
docker build -t api-docs-service:local services/api-docs-service
kubectl apply -f services/api-docs-service/deploy/kubernetes/local.yaml
kubectl -n default port-forward service/api-docs-service 8090:8090
```

Open `http://localhost:8090/swagger-ui/index.html`.

The Kubernetes manifest expects matching Kubernetes Service names for the business services.

## Add a microservice

1. Add `springdoc-openapi-starter-webmvc-ui` version `2.10.1` to the service POM.
2. Add an `@OpenAPIDefinition` with title, description and version.
3. For JWT-protected services, add `@SecurityScheme(name = "bearerAuth", type = HTTP, scheme = "bearer", bearerFormat = "JWT")`.
4. Add one environment mapping in `ApiDocsProperties`.
5. Add one Swagger UI URL entry in `application.yml`.
6. Add one proxy test or smoke probe.
7. Update this README.

## Troubleshooting

If a specification is unavailable:

1. Verify the business service is running.
2. Verify `/v3/api-docs` returns HTTP 200.
3. Verify the matching `*_OPENAPI_URL` environment variable.
4. Verify Kubernetes Service names and ports in local cluster DNS.
5. Check `/specs/{service}` on the docs service for a `502 Bad Gateway` or `404 Not Found`.
