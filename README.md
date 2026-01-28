# kotlin-wordle-training

![CI tests](https://github.com/doctolib/kotlin-wordle-training/actions/workflows/ci_test.yml/badge.svg)

## Introduction

This is a project for the February workshop aimed at playing around with Kotlin basics by building a Wordle clone step by step.

## Getting Started

### Prerequisites

#### dctl (recommended)

[dctl](https://github.com/doctolib/dctl) to run `dctl devenv --profiles common` in order to ensure all the required tools are
installed to fetch various artifacts

#### Java Environment

##### Install Java 21

```shell
brew install openjdk@21
```

##### Install Kotlin 2.0.0

```shell
brew install kotlin@2.0.0
```

##### 7 - Setup Git Hooks

Configure git to use the project's git hooks (for automatic code formatting):

```shell
git config core.hooksPath scripts/git-hooks
```

##### 8 - First Build

Ensure you have the latest maven settings for your workstation:

```shell
dctl devenv --profiles common
```

⚠️ all dependencies will be downloaded, it can take quite some time, (please) be patient.

```shell
./mvnw package
```

### Run tests

```shell
./mvnw clean verify
```

### Run locally

#### Full Stack (Backend + Frontend + Database)

```shell
# Start all services (database, backend, frontend)
docker-compose up

# Or in detached mode (background)
docker-compose up -d

# View logs
docker-compose logs -f frontend
docker-compose logs -f backend

# Stop all services
docker-compose down
```

**Services:**
- **Frontend**: http://localhost:5173
- **Backend**: http://localhost:8080
- **Database**: localhost:55432

#### Backend Only (without Docker)

```shell
# If using database
docker-compose up -d kotlin-wordle-training_postgres
# run migrations
./mvnw -am -pl migrator spring-boot:run -Dspring-boot.run.profiles=dev
# start web server
./mvnw -am -pl application spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Frontend Only (without Docker)

```shell
cd frontend
npm install
npm run dev
```

### Run migrations (if using database)

```shell
./mvnw -am -pl migrator spring-boot:run -Dspring-boot.run.profiles=dev
```

### Using Testcontainers at Development Time

You can run `TestApplication.java` from your IDE directly.
You can also run the application using Maven as follows:

```shell
./mvnw -am -pl application spring-boot:test-run
```

### Code Formatting

[DoctoBoot](https://github.com/doctolib/doctoboot)
uses [Spotless](https://github.com/diffplug/spotless/tree/main/plugin-maven) to apply formatting rules. [ktfmt](https://github.com/facebook/ktfmt) is the Kotlin linter used.

* Code formatting is applied by automatically by `./mwnw clean install`
* To check for formatting errors, run `./mvnw spotless:check`
* To fix locally formatting errors, run `./mvnw spotless:apply`

See the Spotless [documentation](https://github.com/diffplug/spotless/tree/main/plugin-maven#spotlessoff-and-spotlesson)
for more details

For [IntelliJ](https://www.jetbrains.com/idea/buy/) you can use:

* [Spotless Applier](https://plugins.jetbrains.com/plugin/22455-spotless-applier)
* [META ktfmt](https://plugins.jetbrains.com/plugin/14912-ktfmt)

### API Models

API models are defined manually in both backend and frontend:

- **Backend (Kotlin)**: Models are in `application/src/main/kotlin/com/doctolib/kotlinwordletraining/model/`
- **Frontend (TypeScript)**: Models are in `frontend/src/api/models/`

The OpenAPI specification in `api-spec/` serves as documentation and reference, but models are maintained manually to keep full control over the code.

## Workshop Structure

This project is organized into phases for the workshop. Each phase has its own branch with incremental implementation.

**📖 See [WORKSHOP_PHASES.md](./WORKSHOP_PHASES.md) for complete details on:**
- Branch structure and naming
- Implementation phases and requirements
- Database schema
- API specifications

### Phases Overview

- **Phase 1**: Base game functionality (User entity, Database, Word fetcher, Word verification, Controllers, Authentication)
- **Phase 2**: Leaderboard (Ranking algorithm, Leaderboard endpoint)
- **Phase 3**: Scheduled jobs, Kafka, and SSE

## Configuration Files

⚠️ **Important**: Some configuration files are not committed for security reasons:
- `.env` files
- Application properties with sensitive values

**Contact the workshop organizer to get these files before starting.**

## Running with Authentication

To run the full stack with Keymock (OAuth2 provider):

```bash
# Start all services including Keymock
docker-compose --profile authn up

# Or in detached mode
docker-compose --profile authn up -d
```

**Services:**
- **Frontend**: http://localhost:5173
- **Backend**: http://localhost:8080/kotlin-wordle-training
- **Keymock**: http://localhost:8880
- **Database**: localhost:55432

### Useful Links

* Actuator Endpoint: http://localhost:8080/actuator
* API Specification: `api-spec/openapi.yaml`
* Frontend Dev Server: http://localhost:5173

