# AIFinanceAdvisor

Privacy-first AI personal finance advisor — modular, secure, and future-ready.

## 📚 Documentation

- **[REFACTORING_SUMMARY.md](REFACTORING_SUMMARY.md)** - Latest refactoring changes (database + OAuth2)
- **[TESTING_GUIDE.md](TESTING_GUIDE.md)** - Step-by-step testing instructions
- **[KEYCLOAK_SETUP.md](KEYCLOAK_SETUP.md)** - OAuth2/OIDC configuration guide
- **[verify-build.ps1](verify-build.ps1)** - Automated build verification script

## Architecture Overview

- Frontend (Web): React + TypeScript (Tailwind, Framer Motion, Recharts)
- Mobile (Future): Flutter
- API Gateway: Spring Cloud Gateway (Auth + Rate limiting)
- Finance Microservices: Java Spring Boot (secure core)
- AI Engine: Python FastAPI (isolated ML/NLP microservice)
- Databases: PostgreSQL (financial data), MongoDB (logs), Redis (cache)
- Secrets: HashiCorp Vault (optional, future)

Isolated AI: The AI service runs out-of-process from core finance logic to minimize data leakage risk.

## Services (Monorepo)

- gateway/ — Spring Cloud Gateway (routes, rate limit)
- services/finance-service/ — Spring Boot finance API
- services/ai-service/ — FastAPI ML/NLP endpoints
- web/ — React + TypeScript web app

## Security (Now Live)

- Authentication: OAuth 2.0 + JWT via Keycloak (issuer-uri: http://localhost:8888/realms/aifa)
- API Security: Gateway validates JWT tokens; all finance endpoints require authentication
- Service-level: Finance service enforces JWT; AI service allows public health checks
- Encryption: AES-256 utilities and KMS integration (planned)
- Compliance: GDPR-ready; zero-knowledge encryption roadmap

**See [KEYCLOAK_SETUP.md](KEYCLOAK_SETUP.md) for IdP configuration.**

## Database (Now Live)

- PostgreSQL: aifinance database, auto-migrated via Flyway
- Entities: User, Transaction, Goal (with cascading deletes & indexes)
- Connection: finance-service routes to postgresql://postgres:postgres@localhost:5432/aifinance

## Local Dev Infrastructure

docker-compose now includes Keycloak for OAuth2/OIDC:

```bash
# From repo root
docker compose up -d
```

- PostgreSQL: postgres://postgres:postgres@localhost:5432/aifinance
- MongoDB: mongodb://localhost:27017
- Redis: redis://localhost:6379
- Keycloak: http://localhost:8888 (admin/admin)

## Running Services

### 1) AI Service (FastAPI)
```bash
# Windows PowerShell
cd services/ai-service
python -m venv .venv
. .venv/Scripts/Activate.ps1
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8001
```

### 2) Finance Service (Spring Boot)
```bash
cd services/finance-service
mvn spring-boot:run
```

### 3) API Gateway
```bash
cd gateway
mvn spring-boot:run
```

### 4) Web App (React + TS)
```bash
cd web
npm install
npm run dev
```

## Routing (Dev)
- Gateway: http://localhost:8080
  - /api/finance -> finance-service (default http://localhost:8081)
  - /api/ai -> ai-service (default http://localhost:8001)
- Auth endpoints (no token required):
  - GET http://localhost:8080/actuator/health
  - POST http://localhost:8080/api/ai/health
- Protected endpoints (require JWT):
  - All /api/finance/* endpoints

## Quick Start (Full Stack)

1. **Start infrastructure:**
   ```powershell
   cd C:\Users\lohit\Desktop\AIFinanceAdvisor
   docker compose up -d
   ```
   Wait ~30s for Keycloak to initialize.

2. **Set up Keycloak (one-time):**
   - Follow [KEYCLOAK_SETUP.md](KEYCLOAK_SETUP.md)

3. **Start services in separate terminals:**

   **Terminal 1 - AI Service:**
   ```powershell
   cd services\ai-service
   python -m venv .venv
   .\.venv\Scripts\pip install -r requirements.txt
   .\.venv\Scripts\python -m uvicorn app.main:app --port 8001 --reload
   ```

   **Terminal 2 - Finance Service:**
   ```powershell
   cd services\finance-service
   mvn spring-boot:run
   ```

   **Terminal 3 - API Gateway:**
   ```powershell
   cd gateway
   mvn spring-boot:run
   ```

   **Terminal 4 - Web App:**
   ```powershell
   cd web
   npm install
   npm run dev
   ```

4. **Access the app:**
   - Web: http://localhost:5173
   - Keycloak Admin: http://localhost:8888/admin (admin/admin)

## Testing with cURL (After Keycloak Setup)

```bash
# Get JWT token
$token = curl -X POST http://localhost:8888/realms/aifa/protocol/openid-connect/token `
  -H "Content-Type: application/x-www-form-urlencoded" `
  -d "client_id=aifa-web&username=testuser&password=PASSWORD&grant_type=password" | jq -r '.access_token'

# Call protected endpoint
curl http://localhost:8080/api/finance/api/transactions `
  -H "Authorization: Bearer $token"
```

## UI/UX Sections (Targets)
- Onboarding: minimal steps, progress, privacy visuals
- Dashboard: balance, spending ring, savings bar, AI insights
- Expenses: timeline, category filters
- Goals & Planning: trackers, AI timeline
- AI Advisor Chat: conversational guidance

## Next Steps
- Integrate OAuth2/JWT with an IdP (Keycloak/Cloud provider)
- Add DB migrations (Flyway) and JPA entities
- Implement encryption utilities and KMS/Vault integration
- Expand AI models and data contracts
- Add CI/CD (GitHub Actions) and security scans
