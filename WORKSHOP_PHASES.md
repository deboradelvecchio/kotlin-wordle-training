# Workshop Phases - Kotlin Wordle Training

This document describes the workshop phases and corresponding branches. Each component is in its own branch, allowing you to pick and choose what to include.

## Table of Contents

- [Branch Structure](#branch-structure)
- [Phase 1: Base Game Functionality](#phase-1-base-game-functionality)
  - [1. User Entity and Repository](#1-user-entity-and-repository)
  - [2. Database Migrations (Game Entities)](#2-database-migrations-game-entities)
  - [3. Word Fetcher Service](#3-word-fetcher-service)
  - [4. Word Verification Algorithm](#4-word-verification-algorithm)
  - [5. API Controllers](#5-api-controllers)
  - [6. Complete Phase 1](#6-complete-phase-1)
- [Phase 2: Leaderboard](#phase-2-leaderboard)
  - [1. Ranking Algorithm](#1-ranking-algorithm)
  - [2. Leaderboard Endpoint](#2-leaderboard-endpoint)
  - [3. Complete Phase 2](#3-complete-phase-2)
- [Phase 3: Scheduled Jobs, Kafka Events, and SSE](#phase-3-scheduled-jobs-kafka-events-and-sse)
  - [1. Scheduled Jobs](#1-scheduled-jobs)
  - [2. Kafka Setup](#2-kafka-setup)
  - [3. Server-Sent Events (SSE) Endpoint](#3-server-sent-events-sse-endpoint)
  - [4. Daily Leaderboard Aggregation (Optional)](#4-daily-leaderboard-aggregation-optional---for-participants)
  - [5. Complete Phase 3](#5-complete-phase-3)
- [Technical Notes](#technical-notes)
  - [Decisions Made](#decisions-made)
  - [Branch Usage Strategy](#branch-usage-strategy)
  - [SSE Implementation Details](#sse-implementation-details)

## Branch Structure

The project uses a modular branch structure with two types of branches:

### Checkpoint Branches (Starting Points)

These are fully working branches you can start from:

```
main                    → Base setup (frontend complete, backend base)
start-phase-1           → = main (for those who want to build everything from scratch)
start-phase-2           → Phase 1 COMPLETE (for those who want to skip Phase 1)
start-phase-3           → Phase 1+2 COMPLETE (for those who want to skip to Phase 3)
solution-complete       → Everything implemented (final reference)
```

### Solution Branches (Component Solutions)

These contain solutions for individual components. Use them to:
- Compare your implementation
- Copy specific parts you don't want to implement
- Study how something is done

```
solution/phase-1-user-entity         → User entity and repository
solution/phase-1-database-migrations → Database schema and migrations
solution/phase-1-word-fetcher        → External API integration
solution/phase-1-word-verification   → Word validation algorithm
solution/phase-1-controllers         → API endpoints

solution/phase-2-ranking-algorithm   → Ranking calculation
solution/phase-2-leaderboard-endpoint → Leaderboard API

solution/phase-3-scheduled-jobs      → Scheduled word generation
solution/phase-3-kafka-setup         → Kafka producer configuration
solution/phase-3-sse-endpoint        → Server-Sent Events
```

### How to Use

**⚠️ Important:** Never modify the checkpoint or solution branches directly! Always create your own branch from them.

| I want to... | Create branch from... | Implement... |
|--------------|----------------------|--------------|
| Build everything from scratch | `start-phase-1` | Everything |
| Skip Phase 1, do Phase 2+3 | `start-phase-2` | Phase 2 and 3 |
| Only do Kafka/SSE | `start-phase-3` | Only Phase 3 |
| See how X is done | `solution/X` | Nothing, just study |
| Do Phase 1 but skip word-fetcher | `start-phase-1` + merge `solution/phase-1-word-fetcher` | Everything except word-fetcher |

### Example Workflows

**Beginner (wants to learn everything):**
```bash
# Create your own branch from start-phase-1
git checkout -b my-wordle-implementation start-phase-1

# Implement Phase 1 step by step
# If stuck on word-verification, compare with solution:
git diff solution/phase-1-word-verification
```

**Intermediate (wants to focus on Phase 2+3):**
```bash
# Create your own branch from start-phase-2 (Phase 1 already done)
git checkout -b my-wordle-implementation start-phase-2

# Implement Phase 2 and 3
```

**Advanced (only interested in Kafka):**
```bash
# Create your own branch from start-phase-3 (Phase 1+2 already done)
git checkout -b my-kafka-implementation start-phase-3

# Implement only Kafka/SSE
```

**Mix and match:**
```bash
# Create your own branch from start-phase-1
git checkout -b my-wordle-implementation start-phase-1

# Merge solutions for parts you want to skip
git merge solution/phase-1-user-entity
git merge solution/phase-1-database-migrations

# Now implement only word-fetcher, word-verification, and controllers yourself
```

---

## Phase 1: Base Game Functionality

### Objectives
Implement the base Wordle game functionality with persistence and authentication.

### Components

#### 1. User Entity and Repository
**Solution Branch:** `solution/phase-1-user-entity`

**Do we need a User entity in the database?**

Since we use OAuth2 (Keymock) for authentication, you might wonder if we need a User table. Here are the options:

**Option A: User Entity (Recommended)**
- Store user info locally as a cache/reference
- Fields: `id`, `username`, `email`, `external_id`, `created_at`, `updated_at`
- `external_id`: from JWT `sub` claim (identifies user across sessions)
- **Pros:**
  - Fast queries for leaderboard (username already in DB)
  - No need to call Keymock/OAuth2 provider for user info
  - Can add custom fields later (preferences, stats, etc.)
  - Common pattern: OAuth2 for auth, local DB for application data
- **Cons:**
  - Need to sync with OAuth2 provider (username/email might change)
  - Slight duplication of data

**Option B: No User Entity (Alternative)**
- Use `external_id` directly in `GameState` and `GameAttempt` tables
- Extract username/email from JWT token when needed
- **Pros:**
  - Simpler schema
  - No sync needed
- **Cons:**
  - Need to decode JWT for every leaderboard query
  - Username might not be available if token expired
  - Harder to query by username

**Recommendation:** Use Option A (User entity) for better performance and flexibility.

- [ ] Create JPA entity `User`:
  - Fields: `id`, `username`, `email`, `external_id`, `created_at`, `updated_at`
  - `external_id`: from JWT `sub` claim (identifies user across sessions)
- [ ] Create `UserRepository` interface
- [ ] Create Liquibase migration for `users` table
- [ ] Create `UserService`:
  - `findOrCreate(externalId, username, email)`: find or create user
  - `getCurrentUser()`: get user from SecurityContext
- [ ] Create helper `UserUtils` to extract user from JWT token

**OAuth2 Local Setup:**
- DoctoBoot provides local OAuth2 authentication using **Keymock** (Keycloak mock)
- See [KEYMOCK_SETUP.md](./KEYMOCK_SETUP.md) for detailed setup and usage instructions
- **Quick start:** `docker-compose --profile authn up keymock`
- Keymock runs on `http://localhost:8880` with realms `doctolib-pro` and `doctolib-patient`
- Configuration is in `application-dev.yml` with trusted issuers pointing to Keymock

**Files:**
- `application/src/main/kotlin/.../model/User.kt`
- `application/src/main/kotlin/.../repository/UserRepository.kt`
- `application/src/main/kotlin/.../service/UserService.kt`
- `application/src/main/kotlin/.../util/UserUtils.kt`
- `infrastructure/src/main/resources/db/migrations/01-create-users-table.xml`

---

#### 2. Database Migrations (Game Entities)
**Solution Branch:** `solution/phase-1-database-migrations`

- [ ] Create JPA entities:
  - `Word` (id, word, created_at)
    - `created_at`: timestamp when this word was generated
  - `GameAttempt` (id, user_id, word_id, word, attempt_number, feedback, created_at)
  - `GameState` (id, user_id, word_id, state, attempts_count, solved_at, started_at)
- [ ] Create Liquibase migrations for tables
- [ ] Configure JPA relationships:
  - `User` -> `GameState` (OneToMany)
  - `User` -> `GameAttempt` (OneToMany)
  - `Word` -> `GameState` (OneToMany)
  - `Word` -> `GameAttempt` (OneToMany)

**Note on Timestamps:**
- `created_at` on `GameAttempt`: when attempt was made
- `started_at` on `GameState`: when first attempt was made
- `solved_at` on `GameState`: when game was won (null if not won)
- `created_at` on `Word`: when this word was generated (most recent = current valid word)

**Files:**
- `application/src/main/kotlin/.../model/Word.kt`
- `application/src/main/kotlin/.../model/GameAttempt.kt`
- `application/src/main/kotlin/.../model/GameState.kt`
- `infrastructure/src/main/resources/db/migrations/02-create-wordle-tables.xml`

---

#### 3. Word Fetcher Service
**Solution Branch:** `solution/phase-1-word-fetcher`

- [ ] Create `WordFetcherService`:
  - Call `https://random-word-api.herokuapp.com/word?length=5`
  - Handle errors and retry logic
  - Store fetched word in database with `created_at` timestamp
- [ ] Implement word caching logic:
  - Check if word exists for today before fetching
  - Return existing word if available for today
  - Same word for everyone on the same day
  - **Note:** The word is stored server-side only and never sent to clients

**Files:**
- `application/src/main/kotlin/.../service/WordFetcherService.kt`
- `application/src/main/kotlin/.../repository/WordRepository.kt` 

---

#### 4. Word Verification Algorithm
**Solution Branch:** `solution/phase-1-word-verification`

- [ ] Create `WordVerificationService`:
  - Word validation (5 letters, exists in dictionary)
  - Calculate feedback for each letter:
    - `correct`: letter in correct position
    - `present`: letter present but wrong position
    - `absent`: letter not present
- [ ] Handle edge cases (duplicate letters)
- [ ] Create dictionary of valid words (file or database)

**Example:** For word "HELLO" and attempt "HOLES":
- H: correct (position 0)
- O: present (in word but position 1, not 0)
- L: present (in word but position 2, not 1)
- E: present (in word but position 1, not 2)
- S: absent (not in word)

**Files:**
- `application/src/main/kotlin/.../service/WordVerificationService.kt`
- `application/src/main/resources/dictionary/valid-words.txt` (or database table)

---

#### 5. API Controllers
**Solution Branch:** `solution/phase-1-controllers`

- [ ] Create `WordleController` with endpoints:
  - `POST /api/attempt`:
    - Validates word
    - Calculates feedback
    - If authenticated: saves attempt, updates game state
    - Returns feedback and updated state
  - `GET /api/game-state` (requires auth):
    - Returns complete game state (date, attempts, game state)
    - Does NOT return the word itself for security
  - `POST /api/game-state` (requires auth):
    - Saves state from localStorage when user logs in
- [ ] Create `AuthController`:
  - `GET /api/auth/login`: redirects to OAuth2 provider (Keymock in local dev)
  - Configure OAuth2 redirect URLs to work with Keymock
  - After successful login, user is redirected back to application with JWT token
- [ ] Implement saving attempts and statistics:
  - Save each attempt with user ID, word, feedback, attempt number, timestamp
  - Update game state (state, attempts_count, timestamps)
- [ ] **Security Note:** The word is never returned to the client in any endpoint - it's only used server-side to validate attempts

**Files:**
- `application/src/main/kotlin/.../controller/WordleController.kt`
- `application/src/main/kotlin/.../controller/AuthController.kt`
- `application/src/main/kotlin/.../service/GameService.kt` (business logic)

---

#### 6. Complete Phase 1
**Checkpoint Branch:** `start-phase-2` (contains all Phase 1)

Contains all Phase 1 components combined:
- User entity and repository
- Database migrations
- Word fetcher service
- Word verification algorithm
- API controllers

---

## Phase 2: Leaderboard

### Objectives
Implement leaderboard system based on customizable ranking algorithm.

### Components

#### 1. Ranking Algorithm
**Solution Branch:** `solution/phase-2-ranking-algorithm`

- [ ] Define ranking algorithm that combines:
  - Number of attempts (fewer = better)
  - Solve time (faster = better)
- [ ] Possible examples:
  - Score = (7 - attempts) * 1000 - solveTimeSeconds
  - Score = (attempts * 100) + solveTimeSeconds (lower is better)
  - Algorithm is customizable by implementer
- [ ] Calculate solve time: `solved_at - started_at` (in seconds)
- [ ] Create `RankingService` to calculate scores

**Files:**
- `application/src/main/kotlin/.../service/RankingService.kt`

---

#### 2. Leaderboard Endpoint
**Solution Branch:** `solution/phase-2-leaderboard-endpoint`

- [ ] Create `LeaderboardController`:
  - `GET /api/leaderboard`:
    - Returns top N players (e.g., top 10)
    - Each entry: username, attempts, solve time, rank
    - Includes `currentUserRank` if user is in leaderboard
- [ ] Create `LeaderboardService`:
  - Efficient query to calculate ranking
  - Consider pagination if necessary
  - Optimize for performance

**Files:**
- `application/src/main/kotlin/.../controller/LeaderboardController.kt`
- `application/src/main/kotlin/.../service/LeaderboardService.kt`

---

#### 3. Complete Phase 2
**Checkpoint Branch:** `start-phase-3` (contains all Phase 1+2)

Contains all Phase 2 components combined:
- Ranking algorithm
- Leaderboard endpoint

---

## Phase 3: Scheduled Jobs, Kafka Events, and SSE

### Objectives
Implement scheduled jobs for automatic word management, Kafka event publishing, and real-time notifications via Server-Sent Events.

### Components

#### 1. Scheduled Jobs
**Solution Branch:** `solution/phase-3-scheduled-jobs`

- [ ] Create `@Scheduled` job that:
  - Runs every 3 hours
  - Calls external API for new word
  - Saves new word in database with `created_at` timestamp
  - Publishes Kafka event `NEW_WORD_OF_THE_DAY` (if Kafka is set up)
  - Sends SSE notification (if SSE is set up)
- [ ] Configure Spring Scheduling
- [ ] Handle edge cases (check if word was already created recently)

**Files:**
- `application/src/main/kotlin/.../scheduler/WordOfTheDayScheduler.kt`
- `application/src/main/resources/application.yml` (scheduling config)

---

#### 2. Kafka Setup
**Solution Branch:** `solution/phase-3-kafka-setup`

- [ ] Add Kafka dependencies to `pom.xml`
- [ ] Configure Kafka producer
- [ ] Create event model `WordOfTheDayEvent`:
  - word, date, timestamp
- [ ] Add Kafka to `docker-compose.yml`
- [ ] Test event publishing

**Note:** Kafka implementation will be done by workshop participants, but the structure should be prepared.

**Files:**
- `application/src/main/kotlin/.../event/WordOfTheDayEvent.kt`
- `application/src/main/kotlin/.../configuration/KafkaConfiguration.kt`
- `application/src/main/kotlin/.../producer/WordOfTheDayEventProducer.kt`
- `docker-compose.yml` (Kafka service)

---

#### 3. Server-Sent Events (SSE) Endpoint
**Solution Branch:** `solution/phase-3-sse-endpoint`

- [ ] Create SSE endpoint `GET /api/events/word-of-the-day`:
  - Streams events to connected clients
  - Event type: `NEW_WORD_OF_THE_DAY`
  - Event data: `{ type: "NEW_WORD_OF_THE_DAY", date: "2026-01-20", timestamp: 1234567890 }`
- [ ] Implement SSE controller/service:
  - Maintain list of connected clients
  - Broadcast events when new word is generated
  - Handle client disconnections
- [ ] Configure CORS for SSE endpoint
- [ ] Test SSE connection from frontend

**Frontend Integration:**
The frontend already has `useServerSentEvents` hook and `useWordOfTheDayNotifications` hook implemented. The backend needs to:
- Accept SSE connections at `/api/events/word-of-the-day`
- Send `NEW_WORD_OF_THE_DAY` events when scheduled job runs
- Format events as JSON: `{ type: "NEW_WORD_OF_THE_DAY", date: "YYYY-MM-DD", timestamp: number }`

**Files:**
- `application/src/main/kotlin/.../sse/SseController.kt` or `SseService.kt`
- `application/src/main/kotlin/.../sse/SseEventBroadcaster.kt`

---

#### 4. Daily Leaderboard Aggregation (Optional - for participants)
**Solution Branch:** `solution/phase-3-aggregation` (optional)

- [ ] Scheduled job that runs daily (e.g., midnight):
  - Takes all leaderboards from previous day
  - Aggregates data (can use PostgreSQL or MongoDB - participants decide)
  - Cleans detailed data from main database
  - Keeps only aggregated data for history
- [ ] Define aggregated data schema

**Note:** MongoDB setup and aggregation logic will be implemented by workshop participants.

**Files:**
- `application/src/main/kotlin/.../scheduler/DailyAggregationScheduler.kt`
- `application/src/main/kotlin/.../service/LeaderboardAggregationService.kt`

---

#### 5. Complete Phase 3
**Checkpoint Branch:** `solution-complete` (contains everything)

Contains all Phase 3 components combined:
- Scheduled jobs
- Kafka setup (structure)
- SSE endpoint
- Daily aggregation (optional)

---

## Technical Notes

### Decisions Made

1. **Word API**: Uses `https://random-word-api.herokuapp.com/word?length=5` (5-letter words)
2. **Word Management**: 
   - Database uses `Word` entity with `created_at` timestamp
   - Initially, implementations use "word of the day" logic (one word per day, filtering by date)
3. **User Management**:
   - User entity stored in database with `external_id` from JWT `sub` claim
   - First login creates user automatically (find-or-create pattern)
   - User information (username, email) extracted from JWT token
   - `external_id` used to identify same user across different login sessions
4. **Timestamps**:
   - `created_at` on `GameAttempt`: when attempt was made
   - `started_at` on `GameState`: when first attempt was made
   - `solved_at` on `GameState`: when game was won
   - Solve time = `solved_at - started_at` (in seconds)
5. **MongoDB**: Optional, to be implemented by participants for daily aggregation
6. **Kafka**: Structure prepared in Phase 3, implementation by participants

### Branch Usage Strategy

**For Workshop Presenters:**
1. Create checkpoint branches (`start-phase-*`) as starting points
2. Create solution branches (`solution/*`) for each component
3. `solution-complete` contains everything implemented

**For Participants:**
1. **Always create your own branch** from a checkpoint (never modify checkpoint/solution branches)
2. Choose your starting point based on skill level
3. Implement what you want to learn
4. Merge solution branches for parts you want to skip
5. Compare your code with solution branches when stuck

**Creating Your Own Implementation Branch:**
```bash
# Create your own branch from a checkpoint
git checkout -b my-implementation start-phase-2

# Implement what you want, merge what you don't
git merge solution/phase-2-ranking-algorithm

# Now implement only the leaderboard endpoint yourself
```

### SSE Implementation Details

**Backend SSE Endpoint:**
```kotlin
@GetMapping("/events/word-of-the-day", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
fun wordOfTheDayEvents(): SseEmitter {
    // Create and return SseEmitter
    // Store in service to broadcast later
}
```

**Event Format:**
```
data: {"type":"NEW_WORD_OF_THE_DAY","date":"2026-01-20","timestamp":1737417600}

```

**When to Send:**
- When scheduled job generates new word
- Format: JSON with `type`, `date`, and `timestamp` fields
