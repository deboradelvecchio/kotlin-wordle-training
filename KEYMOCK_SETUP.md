# Keymock Setup Guide - Local OAuth2 Authentication

This guide explains how to set up and use Keymock for local OAuth2 authentication in the workshop.

## What is Keymock?

Keymock is a Keycloak mock service that provides local OAuth2 authentication for development and testing. It simulates a real OAuth2 provider (like Keycloak) without needing external services.

## Setup

### 1. Start Keymock

Keymock is configured in `docker-compose.yml` with profile `authn`:

```bash
docker-compose --profile authn up keymock
```

Keymock will run on:
- HTTP: `http://localhost:8880`
- HTTPS: `https://localhost:8443`

### 2. Access Keymock Admin Console

- Open browser: `http://localhost:8880`
- Keymock provides a Keycloak-like admin interface
- Default admin credentials are typically configured in the Keymock image (check Keymock documentation)

### 3. Configure Realms

Keymock comes with two pre-configured realms:
- `doctolib-pro` (for professionals)
- `doctolib-patient` (for patients)

These are already configured in `application-dev.yml`:
```yaml
doctoboot:
  authentication:
    enabled: true
    trusted-issuers:
      - issuer: http://localhost:8880/realms/doctolib-pro
        jwks_uri: http://localhost:8880/realms/doctolib-pro/protocol/openid-connect/certs
      - issuer: http://localhost:8880/realms/doctolib-patient
        jwks_uri: http://localhost:8880/realms/doctolib-patient/protocol/openid-connect/certs
```

## User Management

### Create Users in Keymock

1. Navigate to the realm (e.g., `doctolib-pro` or `doctolib-patient`)
2. Go to **Users** section in the admin console
3. Click **Add user**
4. Fill in:
   - **Username**: e.g., `testuser`
   - **Email**: e.g., `testuser@example.com`
   - **Email Verified**: Enable this
   - **Enabled**: Enable this
5. Go to **Credentials** tab
6. Set a password for the user
7. Save

### User Claims in JWT Token

After login, the JWT token will contain these claims:
- `sub`: Subject (unique user ID) - use this as `external_id` in your database
- `preferred_username`: Username from Keymock
- `email`: User's email address
- `iss`: Issuer (e.g., `http://localhost:8880/realms/doctolib-pro`)

## OAuth2 Login Flow

### 1. User Initiates Login

User clicks login button → Frontend calls:
```
GET /api/auth/login
```

### 2. Backend Redirects to Keymock

Backend redirects to Keymock authorization endpoint:
```
http://localhost:8880/realms/doctolib-pro/protocol/openid-connect/auth
  ?client_id=your-client-id
  &redirect_uri=http://localhost:8080/kotlin-wordle-training/api/auth/callback
  &response_type=code
  &scope=openid profile email
```

### 3. User Logs In

User enters credentials in Keymock login page.

### 4. Keymock Redirects Back

Keymock redirects back to your app with authorization code:
```
http://localhost:8080/kotlin-wordle-training/api/auth/callback?code=AUTHORIZATION_CODE
```

### 5. Backend Exchanges Code for Token

Backend exchanges authorization code for JWT token:
```
POST http://localhost:8880/realms/doctolib-pro/protocol/openid-connect/token
  grant_type=authorization_code
  &code=AUTHORIZATION_CODE
  &client_id=your-client-id
  &client_secret=your-client-secret
  &redirect_uri=http://localhost:8080/kotlin-wordle-training/api/auth/callback
```

### 6. Extract User Info from JWT

From the JWT token, extract:
- `sub` claim → use as `external_id` in your User entity
- `preferred_username` claim → use as `username`
- `email` claim → use as `email`

Then use `UserService.findOrCreate(externalId, username, email)` to create/find user in your database.

## Alternative: Local Token Generator (for Testing)

DoctoBoot provides a local token generator that doesn't require Keymock:

```yaml
doctoboot:
  local-test:
    token-generator.enabled: true
    jwks.enabled: true
```

This is useful for:
- Automated tests
- Development without Keymock UI
- Generating tokens programmatically with custom claims

## Configuration in Application

The application is already configured to use Keymock in `application-dev.yml`. Make sure:

1. `doctoboot.authentication.enabled: true`
2. Trusted issuers point to Keymock realms
3. OAuth2 client is configured in DoctoBoot (check DoctoBoot documentation)

## Troubleshooting

### Keymock not starting
- Check if port 8880 is already in use
- Verify Docker is running
- Check logs: `docker-compose logs keymock`

### Login redirect not working
- Verify Keymock is running: `curl http://localhost:8880/actuator/health`
- Check redirect URI matches in both Keymock client config and backend config
- Verify CORS is configured correctly

### JWT token not validating
- Check issuer matches exactly (including trailing slash)
- Verify JWKS URI is accessible
- Check token expiration time

## References

- Keymock documentation (if available)
- DoctoBoot OAuth2 configuration
- Keycloak documentation (Keymock mimics Keycloak behavior)
