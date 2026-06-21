# Repository Guidelines

## Project Structure & Module Organization

This is a Java 17 Spring Boot backend for an AI learning platform. Main code lives under `src/main/java/com/example/ailearning`. Shared infrastructure is in `common/` for API responses, exceptions, security, logging, config, crypto, and pagination. Business features are grouped by domain under `module/`, including `auth`, `user`, `student`, `teacher`, `course`, `learning`, `fusion`, `ai`, and `role`. Runtime configuration is in `src/main/resources/application*.yml`. MyBatis XML mappers belong in `src/main/resources/mapper/**`. SQL schema, seed data, and migrations are under `database/`; product and API design notes are in `docs/`.

## Build, Test, and Development Commands

- `mvn spring-boot:run` starts the API locally on port `8080` using the active `dev` profile.
- `mvn test` runs unit and integration tests with Spring Boot Test and Spring Security Test.
- `mvn clean package` compiles, tests, and builds the application jar.
- `mvn -DskipTests package` builds the jar without running tests; use only for quick local packaging.

Before running locally, create the MySQL database from `application-dev.yml` (`ai_learning_platform`) and apply `database/schema.sql`, then relevant seed files.

## Coding Style & Naming Conventions

Use 4-space indentation for Java and keep packages lowercase. Follow the existing domain layering: `controller`, `service`, `mapper`, `entity`, `dto`, and `vo`. Name request DTOs as `*Request`, query DTOs as `*Query`, response view objects as `*VO`, and mappers as `*Mapper`. Keep controller logic thin and return the shared `ApiResponse` shape for endpoints. Database fields use snake_case; Java fields use camelCase with MyBatis underscore-to-camel mapping enabled.

## Testing Guidelines

Place tests under `src/test/java/com/example/ailearning`, mirroring the production package. Use `*Test` for focused unit tests and `*IntegrationTest` for Spring context or database-backed flows. Cover service validation, security-sensitive endpoints, mapper queries, and error handling. This repository has test dependencies but no committed test files, so add focused coverage with non-trivial changes.

## Commit & Pull Request Guidelines

Git history currently contains only `init`, so no detailed convention is established. Use short, imperative commit messages such as `add student import validation` or `fix role permission mapping`. Pull requests should include a summary, affected modules, database migration notes, test results, and screenshots or API examples for user-visible behavior. Link related issues or `docs/` entries when applicable.

## Security & Configuration Tips

Do not commit real secrets. Override `APP_JWT_SECRET`, `APP_AES_KEY`, `MYSQL_USERNAME`, and `MYSQL_PASSWORD` through the environment for local and deployed runs. Keep development defaults in YAML suitable only for local use.
