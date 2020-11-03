# Changelog

All notable changes to this project will be documented in this file. 

## [Unreleased]

### Added

- Added timezone to the date in CodeDto.
- Added CodeDto complete validation instead of attribute code in CodeDto class.

### Changed

- Changed Scheme documentation for attribute date in CodeDto. It is not required.

### Fixed

## [1.1.0.RELEASE] - 2020-10-29

### Added

- Setted responses retention time on fake requests.
- AOP aspects ordering. 
- [EU Federation Gateway Service (EFGS)](https://github.com/eu-federation-gateway-service/efgs-federation-gateway) integration using "_One World_" pattern. If EFGS sharing, service creates a new claim for it. 

### Changed

- Log in debug mode fake keys.

### Fixed

- Fixed messages in aspects.

## [1.0.3.RELEASE] - 2020-10-09

### Added

- Configured AWS Parameters Store for service property management.
- Added fake code (Fib sequence) so apps can send fake requests to DP3T.
- Added new test for hashing and verification.
- Added warn logging when code / TAN can't be redeemed.
- Added @Unroll to Spock Framework tests.
- Added CODE_OF_CONDUCT.md file.
- Added CHANGELOG.md file.

### Changed

- Modified exception handling.
- Refactor infection day control, and bug fixed.

### Deleted

- The properties files for Preproduction and Production environments have been removed, because these properties are stored encrypted in the infrastructure (AWS parameter stores).
- Deleted Headers started with "x-forwarded", in server logs.
- Deleted api-docs.yaml file. Yaml available through swagger endpoint.
- Removed Testcontainers annotation.

### Fixed

- Fixed contact email in THIRD-PARTY-NOTICES file

## [1.0.2.RELEASE] - 2020-09-15

* Verification Service. Initial version.

### Added

- Generate codes service, through which the Covid-19 positive confirmation codes are provided to the Autonomous Communities.
- Verification code service, through which the code is verified to be formerly issued by the Health Authority.
- Verification TAN service, through which is checked the TAN provided in the JWT token, which is sent in the positive notification.

[Unreleased]: https://github.com/RadarCOVID/radar-covid-backend-verification-server/compare/1.0.3.RELEASE...develop
[1.0.3.RELEASE]: https://github.com/RadarCOVID/radar-covid-backend-verification-server/compare/1.0.2.RELEASE...1.0.3.RELEASE
[1.0.2.RELEASE]: https://github.com/RadarCOVID/radar-covid-backend-verification-server/releases/tag/1.0.2.RELEASE
