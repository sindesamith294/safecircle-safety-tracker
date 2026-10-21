# Security Policy

## Supported Versions
SafeCircle is maintained on the latest `main` branch.

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |

## Core Privacy Invariants
The security posture of SafeCircle guarantees:
1. **Zero Unapproved Location Disclosures**: Unapproved requests (`PENDING`, `REJECTED`, `EXPIRED`, `REVOKED`) always yield `403 Forbidden` on location queries.
2. **Instant Revocation**: When a user revokes consent, subsequent GPS ingestion records cannot be queried by that contact, regardless of network caching.
3. **Stateless Authorization**: All API endpoints use cryptographic HMAC-SHA256 JWT tokens. Passwords are never stored in plaintext and use adaptive BCrypt hashing.

## Reporting a Vulnerability
If you discover a security vulnerability within SafeCircle, please report it privately by opening a security advisory on GitHub or emailing the maintainer.
