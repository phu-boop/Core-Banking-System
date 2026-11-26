## command line update graphify
- graphify update .
## run service

## identity
./mvnw -f services/identity-service/pom.xml spring-boot:run \
  -Dspring-boot.run.profiles=local

## account
./mvnw -f services/account-service/pom.xml spring-boot:run \
  -Dspring-boot.run.profiles=local

