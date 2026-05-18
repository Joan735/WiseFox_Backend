# 🦊 WiseFox — Backend

> **WiseFox** is a personal finance management application that allows users to track income and expenses, organise their money into shared ledgers, and collaborate with other users.

🔗 **Frontend (Android):** [https://github.com/Junxi-HM/WiseFox](https://github.com/Junxi-HM/WiseFox)

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Architecture](#project-architecture)
- [Data Models](#data-models)
- [API Reference](#api-reference)
- [Security & Authentication](#security--authentication)
- [Environment Variables](#environment-variables)
- [Setup & Running](#setup--running)
- [CI/CD](#cicd)

---

## Overview

The WiseFox backend is a **REST API** built with **Spring Boot 4** and **Java 21**. It manages users, shared ledgers, and financial transactions. It includes JWT-based authentication, **Google OAuth2** social login, and email-based password recovery.

---

## Tech Stack

| Technology | Version |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.2 |
| Spring Security | bundled |
| Spring Data JPA | bundled |
| MySQL | 8+ |
| JWT (jjwt) | 0.12.5 |
| Google API Client | 2.2.0 |
| Lombok | bundled |
| Maven | wrapper included |

---

## Project Architecture

```
src/
└── main/
    └── java/WiseFox/Finance/
        ├── config/          # Security configuration and JWT filter
        ├── controller/      # REST controllers
        ├── dto/
        │   ├── mapper/      # Entity ↔ DTO conversion
        │   ├── request/     # API input objects
        │   └── response/    # API output objects
        ├── model/           # JPA entities (User, Ledger, Transaction, UserLedger)
        ├── repository/      # Spring Data repositories
        └── service/         # Business logic
```

---

## Data Models

### `User`

| Field | Type | Description |
|---|---|---|
| `id` | Long | Primary key |
| `name` | String | First name |
| `surname` | String | Last name |
| `username` | String | Unique username |
| `email` | String | Unique email address |
| `password` | String | BCrypt hash |
| `role` | Enum | `USER` \| `PREMIUM` |
| `pfp` | byte[] | Profile picture (MEDIUMBLOB) |

### `Ledger`

A financial ledger that groups transactions. It has an owner (`User`) and can be shared with other users via the `UserLedger` relationship.

| Field | Type | Description |
|---|---|---|
| `id` | Long | Primary key |
| `name` | String | Ledger name |
| `currency` | String | Currency code (e.g. EUR, USD) |
| `description` | String | Optional description |
| `user` | User | Owner of the ledger |

### `Transaction`

| Field | Type | Description |
|---|---|---|
| `id` | Long | Primary key |
| `amount` | BigDecimal | Transaction amount |
| `type` | Enum | `INCOME` \| `EXPENSE` |
| `category` | Enum | `FOOD`, `TRANSPORT`, `RENT`, `ENTERTAINMENT`, `HEALTH`, `SHOPPING`, `SALARY`, `OTHER` |
| `date` | LocalDate | Transaction date |
| `note` | String | Optional note |
| `ledger` | Ledger | Parent ledger |

### `UserLedger`

Junction table that links users to ledgers and defines the user's permission level.

| Field | Type | Description |
|---|---|---|
| `id` | Long | Primary key |
| `user` | User | Referenced user |
| `ledger` | Ledger | Referenced ledger |
| `permission` | Enum | `OWNER` \| `MEMBER` |

---

## API Reference

### Authentication — `/api/auth`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/register` | Standard registration (multipart/form-data) | ❌ |
| `POST` | `/login` | Login with email and password | ❌ |
| `POST` | `/google` | Login with a Google ID Token | ❌ |
| `POST` | `/verify-code` | Verify 6-digit code (Google flow) | ❌ |
| `POST` | `/register/google` | Complete Google registration | ❌ |
| `POST` | `/forgot-password` | Send password reset code via email | ❌ |
| `POST` | `/verify-reset-code` | Verify password reset code | ❌ |
| `POST` | `/reset-password` | Set a new password | ❌ |

### Users — `/api/user`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/{id}` | Get user by ID | ✅ JWT |
| `GET` | `/username/{username}` | Get user by username | ✅ JWT |
| `GET` | `/{id}/pfp` | Get profile picture | ✅ JWT |
| `PUT` | `/{id}` | Update user profile (multipart) | ✅ JWT |
| `DELETE` | `/{id}` | Delete user | ✅ JWT |

### Ledgers — `/api/ledgers`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/user/{userId}` | List all ledgers for a user | ✅ JWT |
| `GET` | `/{id}` | Get ledger by ID | ✅ JWT |
| `POST` | `/create` | Create a personal ledger | ✅ JWT |
| `POST` | `/create-shared` | Create a shared ledger | ✅ JWT |
| `PUT` | `/{id}` | Update ledger | ✅ JWT |
| `DELETE` | `/{id}` | Delete ledger and all its transactions | ✅ JWT |

### Transactions — `/api/transactions`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/{ledgerId}` | List transactions for a ledger | ✅ JWT |
| `POST` | `/create` | Create a transaction | ✅ JWT |
| `DELETE` | `/delete/{transactionId}` | Delete a transaction | ✅ JWT |

### UserLedger — `/api/userledger`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `GET` | `/ledger/{ledgerId}/members` | List members of a ledger | ✅ JWT |
| `POST` | `/share-by-email` | Share a ledger with another user by email | ✅ JWT |

---

## Security & Authentication

The backend uses **stateless JWT-based authentication**:

1. The client obtains a JWT token after a successful login (standard or Google).
2. All protected routes require the header `Authorization: Bearer <token>`.
3. A `JwtAuthFilter` validates the token on every request before it reaches the controller.
4. Tokens expire after **24 hours** (`jwt.expiration=86400000`).

The Google login flow consists of three steps:
1. The frontend sends the **Google ID Token** → the backend verifies it against Google's API.
2. If the email is not yet registered, a **6-digit code** is sent to the user's email address.
3. The user verifies the code and completes registration with their personal details.

---

## Environment Variables

The following environment variables must be configured (in `application.properties` or as repository secrets for CI):

| Variable | Description |
|---|---|
| `DATABASE_NAME` | JDBC URL for the MySQL database |
| `DATABASE_USER_NAME` | Database username |
| `DATABASE_USER_PASSWORD` | Database password |
| `SECURITY_USER_NAME` | Spring Security basic auth username |
| `SECURITY_USER_PASSWORD` | Spring Security basic auth password |
| `GOOGLE_CLIENT_ID` | Google OAuth2 Client ID |
| `GOOGLE_CLIENT_SECRET` | Google OAuth2 Client Secret |
| `JWT_SECRET` | Secret key used to sign JWT tokens |
| `GMAIL_USER` | Gmail account for sending emails |
| `GMAIL_PASSWORD` | Gmail app password |

---

## Setup & Running

### Prerequisites

- Java 21
- Maven (or use the included wrapper `./mvnw`)
- MySQL 8+

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/Joan735/WiseFox_Backend.git
cd WiseFox_Backend

# 2. Set the required environment variables
export DATABASE_NAME=jdbc:mysql://localhost:3306/wisefox
export DATABASE_USER_NAME=root
export DATABASE_USER_PASSWORD=your_password
# ... set the remaining variables

# 3. Build and run
./mvnw spring-boot:run
```

The server will start at `http://localhost:8080`.

> The database schema is created/updated automatically via `spring.jpa.hibernate.ddl-auto=update`.

### Running Tests

```bash
./mvnw verify
```

Test reports are generated at `target/surefire-reports/`.

---

## CI/CD

The project includes a **GitHub Actions** pipeline (`.github/workflows/ci.yml`) that runs on every push to `master` or `develop`, and on pull requests targeting `master`. It consists of two jobs:

**Build & Test**
- Sets up Java 21 (Temurin distribution)
- Compiles the project and runs all tests with `./mvnw verify`
- Uploads test reports as a build artifact

**Code Quality Check**
- Runs after the build job
- Recompiles with warnings enabled to surface potential quality issues

---

*WiseFox Backend — Track Smart, Live Wisely. 🦊*
