# Kotlin Formatting with ktfmt/Spotless

This project uses **Spotless** with **ktfmt** for Kotlin code formatting.

## Setup in Cursor/VSCode

### 1. Install Kotlin Extension

Cursor will automatically suggest installing the Kotlin extension when you open `.kt` files.

Or install manually:
- **Kotlin Language** (`fwcd.kotlin`)

### 2. Automatic Formatting

Automatic formatting on save is configured in `.vscode/settings.json`.

### 3. Use Tasks for Spotless

To apply Spotless formatting (ktfmt) to the entire project:

1. Open Command Palette (`Cmd+Shift+P` on Mac, `Ctrl+Shift+P` on Windows/Linux)
2. Type "Tasks: Run Task"
3. Select **"Spotless: Apply formatting"**

Alternatively:
- `Cmd+Shift+B` (Mac) or `Ctrl+Shift+B` (Windows/Linux) to open tasks
- Select "Spotless: Apply formatting"

### 4. Check Formatting

To check for formatting errors:
- Task: **"Spotless: Check formatting"**

## Terminal Commands

```bash
# Check formatting
./mvnw spotless:check

# Apply formatting
./mvnw spotless:apply

# Automatic formatting during build
./mvnw clean install
```

## Automatic Formatting on Commit

A **git pre-commit hook** is configured to automatically run `spotless:apply` before every commit.

This ensures that:
- All code is properly formatted before committing
- You don't need to remember to run spotless manually
- Any formatting changes are automatically staged and included in the commit

The hook is located at `.git/hooks/pre-commit` and is automatically executable.

### Bypass the hook (if needed)

If you need to bypass the hook for a specific commit:

```bash
git commit --no-verify -m "your message"
```

## Note

The Kotlin extension uses its internal formatter. To get formatting **exactly identical** to Spotless/ktfmt, use the Maven task `spotless:apply` instead of the editor formatter.
