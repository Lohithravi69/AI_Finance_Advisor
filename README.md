# 🏦 AI Finance Advisor - Production-Ready Financial Management System

> **Status**: Phase 2.5 + 2.1 Complete ✅ | **Latest Version**: February 3, 2026

A comprehensive full-stack financial management application built with **Java 25**, **Spring Boot 3.3.2**, **React 18**, and **PostgreSQL**. Features OAuth2 authentication, budget management, transaction tracking, investment portfolio, and AI-powered financial insights.

## 📚 Documentation

- **[ENHANCEMENTS.md](ENHANCEMENTS.md)** - Complete 10-phase feature roadmap with technical specs
- **[PHASE_2_IMPLEMENTATION.md](PHASE_2_IMPLEMENTATION.md)** - Current implementation details (Phase 2.5 + 2.1)
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - System architecture, data flows, and design patterns
- **[REFACTORING_SUMMARY.md](REFACTORING_SUMMARY.md)** - Database & OAuth2 changes
- **[KEYCLOAK_SETUP.md](KEYCLOAK_SETUP.md)** - OAuth2/OIDC configuration guide
- **[TESTING_GUIDE.md](TESTING_GUIDE.md)** - Testing instructions

## 🚀 Quick Start

### Prerequisites
- Java 25+
- Maven 3.9.12+
- Node.js 16+
- npm 8+

### Installation

**1. Start the Backend Service:**
```bash
cd services/finance-service
mvn spring-boot:run
```
Backend runs on: `http://localhost:8081`

**2. Start the Frontend (New Terminal):**
```bash
cd web
npm install
npm run dev
```
Frontend runs on: `http://localhost:5173`

**3. Access the Application:**
- **Login**: http://localhost:5173/login
- **Profile**: http://localhost:5173/profile
- **Budgets**: http://localhost:5173/budgets
- **Dashboard**: http://localhost:5173/ (protected)

## 🏗️ Architecture Overview

- **Frontend**: React 18 + TypeScript 5.6 (Tailwind, Framer Motion)
- **Backend**: Java 25 + Spring Boot 3.3.2 (REST APIs, JPA)
- **Security**: OAuth2 + JWT (Keycloak integration)
- **Database**: PostgreSQL 16 + Flyway migrations
- **AI Engine**: Python FastAPI (isolated microservice)
- **Gateway**: Spring Cloud Gateway (routing, auth)
- **State Management**: Zustand + localStorage
- **Build**: Maven (backend), Vite (frontend)

## Services (Monorepo)

- **gateway/** — Spring Cloud Gateway (auth + routing)
- **services/finance-service/** — Spring Boot finance API
- **services/ai-service/** — FastAPI ML/NLP endpoints
- **web/** — React + TypeScript web app

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

## ✅ Implemented Features

### Phase 2.5: Authentication & Keycloak Integration
- OAuth2 integration ready
- JWT validation with Spring Security 6
- Protected Routes with auto-redirect
- User Profiles with preferences
- Session Persistence (Zustand + localStorage)
- Demo Mode support

### Phase 2.1: Budget Management
- Create, Update, Delete budgets
- Category-Based Budgeting
- Real-Time Tracking
- Smart Alerts (WARNING/EXCEEDED/RECOVERED)
- Visual Progress Bars
- Responsive Design

## 🔌 API Endpoints

### Authentication
```
POST   /api/auth/login            - OAuth2 Login
POST   /api/auth/logout           - Logout
GET    /api/auth/profile          - Get profile
PUT    /api/auth/profile          - Update profile
GET    /api/auth/preferences      - Get preferences
PUT    /api/auth/preferences      - Update preferences
```

### Budgets
```
POST   /api/budgets               - Create budget
GET    /api/budgets               - List budgets
GET    /api/budgets/active        - Active budgets
GET    /api/budgets/{id}          - Get budget
PUT    /api/budgets/{id}          - Update budget
DELETE /api/budgets/{id}          - Delete budget
GET    /api/budgets/{id}/status   - Budget status
GET    /api/budgets/{id}/alerts   - Budget alerts
```

### Existing Endpoints
```
/api/transactions                 - Transaction management
/api/goals                       - Goal tracking
/api/summary                     - Financial summaries
```

## 🔐 Security

✅ OAuth2 with Keycloak  
✅ JWT token validation  
✅ Spring Security 6  
✅ Protected routes  
✅ User-scoped data  
✅ SQL injection prevention  
✅ CORS ready  
✅ Session timeout  

## 📊 Database

**Current Tables:**
- users (Keycloak integration)
- transactions (income/expense)
- goals (savings tracking)
- budgets (category budgets)
- budget_alerts (threshold alerts)
- user_preferences (settings)

**Migrations:** V1, V2, V3 (Flyway auto-run)

## 🧪 Testing

```bash
# Build & Compile
cd services/finance-service
mvn clean compile

# Frontend Type Checking
cd web
npm run type-check

# Build
mvn clean install
```

✅ 25 source files compile (0 errors)  
✅ Full TypeScript type safety  
✅ Flyway auto-migrations  

## 📈 Performance

| Metric | Value |
|--------|-------|
| Build Time | 8.6s |
| Startup Time | 9.5s |
| Files Compiled | 25 Java |

## 🎯 Remaining Phases

**8 Features Ready to Implement:**

1. **Phase 2.4**: Smart Expense Categorization (auto-categorize, ML)
2. **Phase 2.3**: Income Sources & Multiple Accounts
3. **Phase 2.6**: Reports & Analytics (export, charts)
4. **Phase 2.2**: Investment & Portfolio Tracking
5. **Phase 2.9**: Search & Advanced Filtering
6. **Phase 2.7**: Goals Enhancement (milestones)
7. **Phase 2.8**: Notifications & Alerts
8. **Phase 2.10**: Data Import & Sync (CSV, Plaid)

See **ENHANCEMENTS.md** for full details.

## 📦 Project Structure

```
AIFinanceAdvisor/
├── services/
│   ├── ai-service/           (Python FastAPI)
│   └── finance-service/      (Java Spring Boot)
│       ├── pom.xml
│       ├── src/main/java/com/aifa/finance/
│       │   ├── controller/
│       │   ├── service/
│       │   ├── repository/
│       │   ├── domain/
│       │   └── dto/
│       ├── src/main/resources/
│       │   ├── application.yml
│       │   └── db/migration/
│       └── target/
├── web/                      (React frontend)
│   ├── src/
│   │   ├── pages/
│   │   ├── components/
│   │   ├── stores/
│   │   └── ui/
│   ├── package.json
│   ├── vite.config.ts
│   └── tailwind.config.js
├── gateway/                  (Spring Cloud Gateway)
├── docs/
├── ENHANCEMENTS.md
├── PHASE_2_IMPLEMENTATION.md
├── ARCHITECTURE.md
└── README.md
```

## 🔄 Development Workflow

**Backend Changes:**
1. Modify `services/finance-service/src/main/`
2. Run `mvn clean compile`
3. Changes auto-reload

**Frontend Changes:**
1. Modify `web/src/`
2. Auto-reload in dev server
3. Check console for errors

**Database Changes:**
1. Create `services/finance-service/src/main/resources/db/migration/V#__*.sql`
2. Flyway auto-runs on startup

## 🚀 Deployment

**Environment Variables:**
```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/aifinance
KEYCLOAK_URL=https://your-keycloak
VITE_API_URL=https://api.example.com
```

**Docker:**
```bash
docker build -t aifa-backend services/finance-service/
docker build -t aifa-frontend web/
docker compose up
```

## 📝 Git History

```
f3ca631 - Architecture documentation
4f741ba - Phase 2 implementation summary
97bd3ad - Phase 2.5 + 2.1 complete
012f4d9 - Production REST endpoints
```

## 🐛 Troubleshooting

**Backend issues:**
```bash
mvn clean compile      # Clear cache & rebuild
java -version         # Check Java 25+
```

**Frontend issues:**
```bash
rm -r node_modules && npm install
rm -r .vite            # Clear cache
```

**Database issues:**
- H2 (dev): No setup needed
- PostgreSQL: localhost:5432

## 📞 Support

- Documentation: ENHANCEMENTS.md, ARCHITECTURE.md
- Code Comments: Inline documentation
- Git Log: Change history
- Issues: Create GitHub issue

---

**Latest**: February 3, 2026 | **Version**: 2.1 | **Status**: Phase 2.5 + 2.1 ✅
- Implement encryption utilities and KMS/Vault integration
- Expand AI models and data contracts
- Add CI/CD (GitHub Actions) and security scans
