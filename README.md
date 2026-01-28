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

## Project Status

### ✅ What's Done

#### Frontend

- ✅ React + TypeScript + Vite setup
- ✅ React Router for navigation
- ✅ React Query for API calls
- ✅ Wordle game UI (Board, Keyboard, Game components)
- ✅ Authentication context (Context API)
- ✅ Game state management:
  - Anonymous users: localStorage-based
  - Authenticated users: API-based
- ✅ Automatic state migration when user logs in
- ✅ Docker Compose integration with hot reload

#### Backend

- ✅ Health check endpoint (`GET /api/health`)
- ✅ CORS configuration
- ✅ Spring Security configuration
- ✅ DoctoBoot OAuth2 setup

### 🚧 Backend API Implementation (TODO)

The following endpoints need to be implemented in the backend. All specifications are in `api-spec/openapi.yaml`.

**📖 For detailed implementation phases and branch structure, see [WORKSHOP_PHASES.md](./WORKSHOP_PHASES.md)**

#### 1. `POST /api/attempt`

**Status:** ❌ Not implemented

**Requirements:**
- Accepts `AttemptRequest` with a 5-letter word
- Validates word exists in dictionary (5 letters, valid word)
- Calculates feedback for each letter:
- `correct`: letter is in correct position
- `present`: letter is in word but wrong position
- `absent`: letter is not in word
- **If authenticated:**
- Save attempt to database
- Update game state
- If game is won: return `solvedWord` in response
- Return `attempts` array in response (to avoid extra API call)
- **If NOT authenticated:**
- Only validate word and return feedback
- Do not save anything
- Do not return `solvedWord` or `attempts`

**Response Schema:** `AttemptResponse`

**Error Cases:**
- `400`: Invalid word (not 5 letters or not in dictionary)
- `422`: Game already finished or no attempts remaining (max 6 attempts)

**Security Notes:**
- The word is never returned to the client - it's only used server-side to validate attempts
- Word validation logic: check if word exists in a dictionary
- Feedback calculation: compare attempted word with current word
- Game ends when: word is correct (won) or 6 attempts used (lost)

---

#### 2. `GET /api/game-state`

**Status:** ❌ Not implemented

**Requirements:**
- **Requires authentication** (returns 401 if not authenticated)
- Returns current game state for authenticated user
- Includes all attempts made so far
- Includes date (but NOT the word itself for security)

**Response Schema:** `GameStateResponse`

**Security:**
- Must check authentication token
- Return only current user's game state
- **Never return the word** - it's only used server-side

---

#### 3. `POST /api/game-state`

**Status:** ❌ Not implemented

**Requirements:**
- **Requires authentication** (returns 401 if not authenticated)
- Accepts `SaveGameStateRequest` with attempts array and date
- Saves game state from localStorage when user logs in
- Transfers anonymous attempts to authenticated user
- Only saves if date matches current word date

**Request Schema:** `SaveGameStateRequest`

**Response:** `200 OK` (no body)

**Security:**
- Must check authentication token
- Only save for authenticated user

---

#### 4. `GET /api/leaderboard`

**Status:** ❌ Not implemented

**Requirements:**
- **Requires authentication** (returns 401 if not authenticated)
- Returns leaderboard with top players
- Each entry includes:
- `username`
- `attempts` (number of attempts used)
- `solveTimeSeconds` (time to solve in seconds)
- `rank` (position in leaderboard)
- Optionally includes `currentUserRank` if user is in leaderboard

**Response Schema:** `LeaderboardResponse`

**Ranking Logic:**
- Backend decides ranking algorithm (combines attempts and solve time)
- Example: fewer attempts = better, faster solve time = better
- Algorithm is up to backend implementation

**Security:**
- Must check authentication token

---

#### 6. `GET /api/auth/login`

**Status:** ⚠️ Partially implemented (DoctoBoot handles OAuth2)

**Requirements:**
- Redirects to DoctoBoot OAuth2 provider
- After login, redirects back to application
- Sets authentication token/cookie

**Notes:**
- DoctoBoot should handle most of this automatically
- May need to configure redirect URLs

---

### Implementation Notes

1. **Word Dictionary**: You'll need a dictionary of valid 5-letter words for validation
2. **Word Selection**: Words are fetched from `https://random-word-api.herokuapp.com/word?length=5`
3. **Security**: The word is **never** returned to the client - it's only stored server-side and used to validate attempts
4. **Authentication**: Use DoctoBoot's authentication mechanisms to check if user is authenticated
5. **Database**:
   - User entity with `external_id` from JWT `sub` claim
   - Word entity with `created_at` timestamp (most recent word is current)
   - GameState and GameAttempt entities for tracking game progress
   - See [WORKSHOP_PHASES.md](./WORKSHOP_PHASES.md) for detailed schema
6. **Error Handling**: Return appropriate HTTP status codes and error messages

### Workshop Structure

This project is organized into phases for the workshop. Each phase has its own branch with specific components:

- **Phase 1**: Base game functionality (User entity, Database, Word fetcher, Word verification, Controllers)
- **Phase 2**: Leaderboard (Ranking algorithm, Leaderboard endpoint)
- **Phase 3**: Scheduled jobs, Kafka, and SSE (Scheduled word generation, Kafka events, Server-Sent Events)

See [WORKSHOP_PHASES.md](./WORKSHOP_PHASES.md) for complete details on branch structure and implementation phases.

### Useful Links

* Actuator Endpoint: http://localhost:8080/actuator
* API Specification: `api-spec/openapi.yaml`
* Frontend Dev Server: http://localhost:5173

