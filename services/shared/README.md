# Shared contracts

This module contains framework-independent API contracts that are shared by
FlickPay services.

## Publishing locally

From this directory, install the artifact into the local Maven repository:

```bash
cd services/shared/app
mvn install
```

Services can then depend on `com.flickpay:shared-contracts:0.0.1-SNAPSHOT`.

The module intentionally does not depend on Spring or on any service domain.

After changing a shared contract, reinstall the artifact and reload the Maven
project in the IDE before compiling a consuming service:

```bash
cd services/shared/app
mvn install
cd ../../user/app
./mvnw clean test
```
