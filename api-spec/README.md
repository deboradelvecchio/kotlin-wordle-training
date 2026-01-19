# API Specification

This directory contains the OpenAPI specification for the Wordle Training API.

## Purpose

The OpenAPI specification serves as **documentation and reference** for the API contract. Models are maintained manually in both backend and frontend to keep full control over the code.

## Structure

- `openapi.yaml` - Main OpenAPI 3.0 specification file
- `schemas/` - Reusable schema definitions organized by domain

## Manual Model Maintenance

When you update the API specification:

1. **Update the OpenAPI spec** in `api-spec/` (for documentation)
2. **Manually update the models** in:
   - **Backend (Kotlin)**: `application/src/main/kotlin/com/doctolib/kotlinwordletraining/model/`
   - **Frontend (TypeScript)**: `frontend/src/api/models/`

This approach gives you full control over the generated code and allows for customizations that code generators might not support.

## Example

```yaml
components:
  schemas:
    Game:
      type: object
      properties:
        id: { type: string, format: uuid }
        word: { type: string, minLength: 5, maxLength: 5 }
        attempts: { type: integer, minimum: 0, maximum: 6 }
      required: [id, word, attempts]
```

After updating the spec, manually create/update:
- **Kotlin**: `com.doctolib.kotlinwordletraining.model.Game`
- **TypeScript**: `frontend/src/api/models/Game.ts`
