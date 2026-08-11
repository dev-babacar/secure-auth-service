# 🔐 Secure Auth Service

[![CI](https://github.com/dev-babacar/secure-auth-service/actions/workflows/ci.yml/badge.svg)](https://github.com/dev-babacar/secure-auth-service/actions/workflows/ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=dev-babacar_secure-auth-service&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=dev-babacar_secure-auth-service)

Service d'authentification et d'autorisation complet implémenté avec **Spring Boot 3.5**, **Spring Authorization Server**, et une **architecture hexagonale** stricte.

---

## 📋 Fonctionnalités

- ✅ Inscription avec validation et hachage BCrypt
- ✅ Authentification OAuth2 avec JWT signé RS256
- ✅ Refresh token avec rotation et détection de vol
- ✅ MFA TOTP compatible Google Authenticator
- ✅ Révocation de token via blacklist Redis
- ✅ Audit log de toutes les actions sensibles
- ✅ Gestion des erreurs RFC 7807 (ProblemDetail)

---

## 🏗️ Architecture

Architecture hexagonale (Ports & Adapters) — le domaine métier est isolé de toute dépendance technique.

```
src/main/java/com/babacar/secureauthservice/
├── domain/                    ← zéro annotation Spring
│   ├── model/                 (User, Role, RefreshToken, MfaSecret)
│   ├── port/
│   │   ├── in/                (use cases : interfaces)
│   │   └── out/               (repositories, cache : interfaces)
│   └── service/               (logique métier pure)
├── application/
│   └── usecase/               (implémentation des use cases)
├── adapter/
│   ├── in/web/                (controllers REST + DTOs)
│   └── out/
│       ├── persistence/       (JPA + PostgreSQL)
│       ├── cache/             (Redis blacklist)
│       ├── token/             (JWT encoder)
│       └── mail/              (Mailhog SMTP)
└── config/                    (Spring Security, OAuth2, JWT)
```

---

## 🛠️ Stack technique

| Couche | Technologie |
|---|---|
| Framework | Spring Boot 3.5.14 |
| Sécurité | Spring Authorization Server 1.5 |
| Token | JWT RS256 (NimbusJwt) |
| MFA | dev.samstevens.totp 1.7.1 |
| Persistence | Spring Data JPA + PostgreSQL 16 |
| Migration | Flyway 10 |
| Cache | Redis 7 |
| Tests | JUnit 5 + Mockito + Testcontainers |
| Build | Maven 3.9 + Java 21 |
| CI | GitHub Actions |
| Qualité | SonarCloud + JaCoCo |

---

## 🚀 Démarrage rapide

### Prérequis

- Java 21
- Docker Desktop
- Maven 3.9+

### Lancer le projet

```bash
# 1. Cloner le repo
git clone https://github.com/dev-babacar/secure-auth-service.git
cd secure-auth-service

# 2. Configurer les variables d'environnement
cp .env.example .env
# Éditer .env avec vos valeurs

# 3. Démarrer l'infrastructure
docker compose up -d

# 4. Lancer l'application
mvn spring-boot:run
```

L'application démarre sur `http://localhost:8080`.

---

## 📡 Endpoints

| Méthode | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Inscription | Public |
| POST | `/api/auth/login` | Connexion | Public |
| POST | `/api/auth/refresh` | Rotation refresh token | Public |
| POST | `/api/auth/logout` | Déconnexion + blacklist | Public |
| POST | `/api/auth/mfa/setup` | Configurer le MFA | Public |
| POST | `/api/auth/mfa/verify` | Valider le code TOTP | Public |
| GET | `/api/auth/me` | Endpoint protégé (test) | JWT |
| POST | `/oauth2/token` | Token OAuth2 | Basic Auth |

---

## 🧪 Tests

```bash
# Lancer tous les tests
mvn test

# Lancer avec rapport JaCoCo
mvn verify

# Voir le rapport de couverture
open target/site/jacoco/index.html
```

---

## 📐 Décisions techniques (ADR)

| ADR | Décision | Raison |
|---|---|---|
| ADR-001 | Spring Authorization Server vs Keycloak | Prouve la compréhension d'OAuth2 en profondeur |
| ADR-002 | JWT signé RS256 vs HS256 | Clé publique partageable via JWKS, pas de secret partagé |
| ADR-003 | Blacklist JWT dans Redis | TTL natif, moins de 1ms, nettoyage automatique |
| ADR-004 | UserEntity séparée de User domain | Isolation domaine — changer de DB ne touche pas le domaine |
| ADR-005 | Refresh token rotation + détection de vol | Token réutilisé = famille révoquée, standard OAuth 2.1 |

---

## 🏛️ Flux d'inscription

```
POST /api/auth/register
        ↓
AuthController (adapter/in/web)
        ↓
RegisterUserUseCase (domain/port/in)
        ↓
RegisterUserUseCaseImpl (application/usecase)
        ↓
AuthService — vérifie email unique (domain/service)
        ↓
UserRepository (domain/port/out)
        ↓
UserPersistenceAdapter — traduit User ↔ UserEntity
        ↓
PostgreSQL — INSERT INTO users
        ↓
AuditLog — REGISTER_SUCCESS
        ↓
HTTP 201 Created
```

---

## 📁 Structure du projet

```
.
├── src/
│   ├── main/
│   │   ├── java/               ← code source
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/   ← Flyway V1..V4
│   └── test/
│       └── java/               ← 49 tests
├── docs/
│   └── adr/                    ← Architecture Decision Records
├── docker-compose.yml
├── .env.example
├── sonar-project.properties
└── pom.xml
```

---

## 📄 Licence

MIT
