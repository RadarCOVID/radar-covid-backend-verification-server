# RadarCOVID Verification Service

<p align="center">
    <a href="https://github.com/RadarCOVID/radar-covid-backend-verification-server/commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/RadarCOVID/radar-covid-backend-verification-server?style=flat"></a>
    <a href="https://github.com/RadarCOVID/radar-covid-backend-verification-server/issues" title="Open Issues"><img src="https://img.shields.io/github/issues/RadarCOVID/radar-covid-backend-verification-server?style=flat"></a>
    <a href="https://github.com/RadarCOVID/radar-covid-backend-verification-server/blob/master/LICENSE" title="License"><img src="https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg?style=flat"></a>
</p>

## Introduction

Verification Service in terms of the Radar COVID project enables:

- Autonomous Communities are able to request verification codes so then they can give them to COVID-19 patients.
- Once COVID-19 patients have the verification code, they can use the mobile application to send this verification code so Verification Service can:
    - Check if the verification code is correct, exists in database, is not redeemed and is not expired.
    - Once verified, Verification Service returns a [JSON Web Token (JWT)](https://jwt.io/) so mobile application can send the exposition keys to the DP3T service.

## Prerequisites

These are the frameworks and tools used to develop the solution:

- [Java 11](https://openjdk.java.net/).
- [Maven](https://maven.apache.org/).
- [Spring Boot](https://spring.io/projects/spring-boot) version 2.3.
- [Lombok](https://projectlombok.org/), to help programmer. Developers have to include the IDE plugin to support Lombok features (ie, for Eclipse based IDE, go [here](https://projectlombok.org/setup/eclipse)).
- [ArchUnit](https://www.archunit.org/) is used to check Java architecture.
- [PostgreSQL](https://www.postgresql.org/).
- Testing:
    - [Spock Framework](http://spockframework.org/).
    - [Docker](https://www.docker.com/), because of using Testcontainers.
    - [Testcontainers](https://www.testcontainers.org/).

## Installation and Getting Started

### Sample code

Before you continue reading, let us let you know that there is sample code:

- [`CheckSumUtil`](./verification-server-api/src/main/java/es/gob/radarcovid/verification/util/CheckSumUtil.java). Both methods (`checkSum` and `validateChecksum`) are not real in order to avoid Production requests, since _some people_ is sending incorrect verification codes.
- [`JwtAuthorizationFilter`](./verification-server-service/src/main/java/es/gob/radarcovid/verification/security/JwtAuthorizationFilter.java). These properties are not real in order to avoid _illegal_ requests: `AUTHORIZATION_HEADER`, `AUTHORIZATION_BEARER` and `RADAR_PREFIX`.

### Building from Source

To build the project, you need to run this command:

```shell
mvn clean package -P<environment>
```

Where `<environment>` has these possible values:

- `local-env`. To run the application from local (eg, from IDE o from Maven using `mvn spring-boot:run`). It is the default profile, using [`application-local.yml`](./verification-server-boot/src/main/resources/application-local.yml) configuration file.
- `docker-env`. To run the application in a Docker container with `docker-compose`, using [`application-docker.yml`](./verification-server-boot/src/main/resources/application-docker.yml) configuration file.
- `pre-env`. To run the application in the Preproduction environment, using [`application-pre.yml`](./verification-server-boot/src/main/resources/application-pre.yml) configuration file.
- `pro-env`. To run the application in the Production environment, using [`application-pro.yml`](./verification-server-boot/src/main/resources/application-pro.yml) configuration file.

All profiles will load the default [configuration file](./verification-server-boot/src/main/resources/application.yml).

Private and public keys located on [`application-local.yml`](./verification-server-boot/src/main/resources/application-local.yml) and [`application-docker.yml`](./verification-server-boot/src/main/resources/application-docker.yml) are only for testing on local (running inside IDE or Docker).

### Running the Project

Depends on the environment you selected when you built the project, you can run the project:

- From the IDE, if you selected `local-env` environment (or you didn't select any Maven profile).
- From Docker. Once you build the project, you will have in `verification-server-boot/target/docker` the files you would need to run the application from a container (`Dockerfile` and the Spring Boot fat-jar).

If you want to run the application inside a docker in local, once you built it, you should run:

```shell
docker-compose up -d postgres
docker-compose up -d backend
```

#### Database

This project doesn't use either [Liquibase](https://www.liquibase.org/) or [Flyway](https://flywaydb.org/) because:

1. DB-Admins should only have database privileges to maintain the database model ([DDL](https://en.wikipedia.org/wiki/Data_definition_language)).
2. Applications should only have privileges to maintain the data ([DML](https://en.wikipedia.org/wiki/Data_manipulation_language)).

Because of this, there are two scripts:

- [`01-VERIFICATION-DDL.sql`](./sql/01-VERIFICATION-DDL.sql). Script to create the model.
- [`02-VERIFICATION-DML.sql`](./sql/02-VERIFICATION-DML.sql). Script with inserts. This file (and also [`data.sql`](./verification-server-boot/src/test/resources/data.sql)) contains sample data for record `01` corresponding to `Andalucía`.

### API Documentation

Along with the application there comes with [OpenAPI Specification](https://www.openapis.org/), which you can access in your web browser when the Verification Service is running (unless in Production environment, where it is inactive by default):

```shell
<base-url>/openapi/api-docs
```

If running in local, you can get the OpenAPI accessing http://localhost:8080/openapi/api-docs. You can download the YAML version in `/openapi/api-docs.yaml`.

You can get a copy [here](./verification-server-api/api-docs.yaml).

#### Endpoints

| Endpoint | Description |
| -------- | ----------- |
| `/generate?n=<number>` | Generates `n` verification codes to be used by Autonomous Communities |
| `/verification/code` | Verify provided code |

### Generate codes

When an Autonomous Community (CCAA) asks for generating `n` codes to the Verification Service, firstly the CCAA needs to generate a private key to create the [JSON Web Token (JWT)](https://jwt.io) and sends the corresponding public key to the Verification Service.

#### Keys generation

This service uses [Elliptic Curve (EC)](https://en.wikipedia.org/wiki/Elliptic-curve_cryptography) keys to allow Autonomous Communities to request verification codes and to sign the given response.

To generate the keys you can use these commands ([OpenSSL](https://www.openssl.org/) tool is required):

1. Generate private key:
    ```shell
    openssl ecparam -name secp521r1 -genkey -noout -out generated_private.pem
    ```
2. Converse private key to new PEM format:
    ```shell
    openssl pkcs8 -topk8 -inform pem -in generated_private.pem -outform pem -nocrypt -out generated_private_new.pem
    ```
3. Get Base64 from private key:
    ```shell
    openssl base64 -in generated_private_new.pem > generated_private_base64.pem
    ```
4. Generate public key:
    ```shell
    openssl ec -in generated_private_new.pem -pubout -out generated_pub.pem
    ```
5. Get Base64 from public key:
    ```shell
    openssl base64 -in generated_pub.pem > generated_pub_base64.pem
    ```

#### Creating JWT from CCAA

Once CCAA has generated its private/public keys, CCAA has to send the public key to the Verification Service. The Verification Service will save this public key in the `VERIFICATION.CCAA` table, with the record that corresponds with the code (`DE_CCAA_ID`) for that CCAA.

The CCAA will create a JWT code (ie, [`CCAATokenGeneratorTest.java`](./verification-server-boot/src/test/java/es.gob.radarcovid.verification.test.CCAATokenGeneratorTest.java)) and use it to invoke to `/generate` endpoint. The JWT token has to be in the `X-RadarCovid-Authorization` header (NOTE: the name of this header is an example; in Production environment, it is different).

#### Generation codes signature

The `generate` response has a `signature` so client applications can verify if the response is valid. Verification Service will provide his public key to the CCAA so then they are able to check the signature.

[`GenerationControllerTestSpec`](./verification-server-boot/src/test/groovy/es/gob/radarcovid/verification/controller/test/GenerationControllerTestSpec.groovy) is a good example that shows how it works.

### Modules

Verification Service has four modules:

- `verification-server-parent`. Parent Maven project to define dependencies and plugins.
- `verification-server-api`. [DTOs](https://en.wikipedia.org/wiki/Data_transfer_object) exposed.
- `verification-server-boot`. Main application, global configurations and properties. This module also has integration tests and Java architecture tests with ArchUnit:
- `verification-server-service`. Business and data layers.

## Support and Feedback
The following channels are available for discussions, feedback, and support requests:

| Type       | Channel                                                |
| ---------- | ------------------------------------------------------ |
| **Issues** | <a href="https://github.com/RadarCOVID/radar-covid-backend-verification-server/issues" title="Open Issues"><img src="https://img.shields.io/github/issues/RadarCOVID/radar-covid-backend-verification-server?style=flat"></a> |

## Contribute

If you want to contribute with this exciting project follow the steps in [How to create a Pull Request in GitHub](https://opensource.com/article/19/7/create-pull-request-github).

More details in [CONTRIBUTING.md](./CONTRIBUTING.md).

## License

This Source Code Form is subject to the terms of the [Mozilla Public License, v. 2.0](https://www.mozilla.org/en-US/MPL/2.0/).
