# Contributing to SafeCircle

Thank you for your interest in contributing to SafeCircle!

## Code Architecture & Philosophy
SafeCircle is engineered around strict **privacy-first consent enforcement**:
- A phone number identifies a registered user, but **never reveals their coordinates** without explicit active consent.
- Permission must be checked on **every read** of location data, not merely upon circle creation or initial connection.
- When permission is revoked by the owner, location access for the viewer stops **immediately**.

## Development Setup
1. Fork and clone the repository.
2. Ensure you have **Java 21+** (or Java 17+) and **Python 3.10+**.
3. Verify all tests pass before making modifications:
   ```bash
   cd backend
   ./mvnw test
   ```

## Branch & Pull Request Guidelines
1. Create a feature branch: `git checkout -b feature/your-feature-name`.
2. Follow standard Spring Boot and Java coding standards:
   - Meaningful commit messages using Conventional Commits (`feat:`, `fix:`, `docs:`, `test:`).
   - Ensure new services and endpoints have corresponding JUnit tests.
3. Submit a Pull Request targeting `main`.
