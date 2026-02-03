# AI Finance Advisor - Phase 2 Architecture Overview

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          CLIENT LAYER (React + Vite)                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                    Pages & Components                               │  │
│  ├──────────────────────────────────────────────────────────────────────┤  │
│  │  • LoginPage → OAuth2 form with JWT token capture                   │  │
│  │  • ProfilePage → Edit profile, preferences, logout                  │  │
│  │  • BudgetsPage → View & manage budgets                              │  │
│  │  • BudgetCard → Visual progress display                             │  │
│  │  • CreateBudgetForm → Modal for new budget creation                 │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                ↓                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                   State Management (Zustand)                        │  │
│  ├──────────────────────────────────────────────────────────────────────┤  │
│  │  • authStore → token, user, isAuthenticated, login/logout/setUser  │  │
│  │  • localStorage → persistent session data                           │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                ↓                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                   Route Protection                                  │  │
│  ├──────────────────────────────────────────────────────────────────────┤  │
│  │  • ProtectedRoute wrapper → checks isAuthenticated                  │  │
│  │  • Redirects to /login if not authenticated                         │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                ↓                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │             HTTP Client + JWT Token Injection                       │  │
│  ├──────────────────────────────────────────────────────────────────────┤  │
│  │  Authorization: Bearer <JWT_TOKEN>                                  │  │
│  │  Content-Type: application/json                                     │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ↓
                            (HTTPS in production)
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│                      API GATEWAY / SPRING SECURITY                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  JWT Token Validation:                                                     │
│  • Verify signature with Keycloak public key                               │
│  • Extract user claims (sub, email, name)                                  │
│  • Validate issuer and expiration                                          │
│  → Inject Jwt object into @AuthenticationPrincipal                         │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│                    APPLICATION LAYER (Spring Boot 3.3.2)                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │              Controllers (REST Endpoints)                           │  │
│  ├──────────────────────────────────────────────────────────────────────┤  │
│  │                                                                      │  │
│  │  AuthController:                                                   │  │
│  │  • POST   /api/auth/login           → create LoginResponse         │  │
│  │  • POST   /api/auth/logout          → invalidate session           │  │
│  │  • GET    /api/auth/profile         → return UserProfileDto        │  │
│  │  • PUT    /api/auth/profile         → update User entity           │  │
│  │  • GET    /api/auth/preferences     → return UserPreferences       │  │
│  │  • PUT    /api/auth/preferences     → update preferences           │  │
│  │                                                                      │  │
│  │  BudgetController:                                                 │  │
│  │  • POST   /api/budgets              → BudgetService.createBudget() │  │
│  │  • GET    /api/budgets              → BudgetService.getBudgetsByUser() │
│  │  • GET    /api/budgets/active       → BudgetService.getActiveBudgets() │
│  │  • GET    /api/budgets/{id}         → BudgetService.getBudget()   │  │
│  │  • PUT    /api/budgets/{id}         → BudgetService.updateBudget()│  │
│  │  • DELETE /api/budgets/{id}         → BudgetService.deleteBudget()│  │
│  │  • GET    /api/budgets/{id}/status  → BudgetService.getBudgetStatus() │
│  │  • GET    /api/budgets/{id}/alerts  → BudgetService.getBudgetAlerts() │
│  │                                                                      │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                ↓                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │              Services (Business Logic)                              │  │
│  ├──────────────────────────────────────────────────────────────────────┤  │
│  │                                                                      │  │
│  │  AuthService:                                                       │  │
│  │  • getOrCreateUser(Jwt) → Creates user from Keycloak ID            │  │
│  │  • getUserProfile(userId) → Returns UserProfileDto                 │  │
│  │  • updateUserProfile(userId, dto) → Updates User entity            │  │
│  │  • getUserPreferences(userId) → Returns UserPreferences            │  │
│  │  • updateUserPreferences(userId, prefs) → Updates preferences      │  │
│  │  • createLoginResponse(Jwt) → Returns LoginResponse with token     │  │
│  │                                                                      │  │
│  │  BudgetService:                                                     │  │
│  │  • createBudget(userId, request) → saves Budget entity             │  │
│  │  • getBudgetsByUser(userId) → queries all budgets                  │  │
│  │  • getActiveBudgets(userId, date) → active on date                 │  │
│  │  • updateBudget(budgetId, userId, request) → updates entity        │  │
│  │  • deleteBudget(budgetId, userId) → cascades to alerts             │  │
│  │  • updateSpentAmount(budgetId, amount) → checks thresholds         │  │
│  │  • checkAndCreateAlerts(budget, previousAmount) → logic            │  │
│  │  • getBudgetAlerts(budgetId, userId) → returns alert list          │  │
│  │                                                                      │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                ↓                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │              Repositories (Data Access)                             │  │
│  ├──────────────────────────────────────────────────────────────────────┤  │
│  │                                                                      │  │
│  │  UserRepository:                                                    │  │
│  │  • findByKeycloakId(String) → Optional<User>                       │  │
│  │  • findByEmail(String) → Optional<User>                            │  │
│  │  • JpaRepository base → save(), findById(), delete()               │  │
│  │                                                                      │  │
│  │  UserPreferencesRepository:                                         │  │
│  │  • findByUserId(Long) → Optional<UserPreferences>                  │  │
│  │  • JpaRepository base → standard CRUD                              │  │
│  │                                                                      │  │
│  │  BudgetRepository:                                                  │  │
│  │  • findByUserId(Long) → List<Budget>                               │  │
│  │  • findByIdAndUserId(Long, Long) → Optional<Budget>                │  │
│  │  • findActiveBudgetsByUserAndDate(...) → custom @Query             │  │
│  │                                                                      │  │
│  │  BudgetAlertRepository:                                             │  │
│  │  • findByBudgetId(Long) → List<BudgetAlert>                        │  │
│  │  • findByBudgetIdOrderByTriggeredAtDesc(...) → sorted alerts       │  │
│  │                                                                      │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                ↓                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │              Entities (Domain Models)                               │  │
│  ├──────────────────────────────────────────────────────────────────────┤  │
│  │                                                                      │  │
│  │  User (1 → Many):                                                   │  │
│  │  • id, email, keycloakId, fullName, monthlyIncome                  │  │
│  │  • Relationships: transactions, goals, preferences                  │  │
│  │                                                                      │  │
│  │  UserPreferences (1 ← → 1):                                         │  │
│  │  • currencyPreference, timezone                                    │  │
│  │  • emailNotifications, pushNotifications                            │  │
│  │  • budgetAlerts, goalNotifications, investmentUpdates              │  │
│  │                                                                      │  │
│  │  Budget (1 → Many):                                                 │  │
│  │  • id, user_id, name, category, monthlyLimit                       │  │
│  │  • spentAmount, startDate, endDate, alertThreshold                 │  │
│  │  • Relationship: alerts (cascading delete)                          │  │
│  │  • Method: getPercentageSpent()                                     │  │
│  │                                                                      │  │
│  │  BudgetAlert (Many → 1):                                            │  │
│  │  • id, budget_id, alertType (enum), percentage, triggeredAt         │  │
│  │  • Types: WARNING, EXCEEDED, RECOVERED                              │  │
│  │                                                                      │  │
│  │  Transaction, Goal (existing):                                      │  │
│  │  • Already implemented in Phase 1                                   │  │
│  │                                                                      │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│                         DATA LAYER (Persistence)                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                  Flyway Database Migrations                         │  │
│  ├──────────────────────────────────────────────────────────────────────┤  │
│  │                                                                      │  │
│  │  V1__Initial_Schema.sql:                                            │  │
│  │  • CREATE TABLE users, transactions, goals                          │  │
│  │                                                                      │  │
│  │  V2__Add_User_Preferences.sql:                                      │  │
│  │  • CREATE TABLE user_preferences (1:1 with users)                   │  │
│  │  • Indexes on user_id                                               │  │
│  │                                                                      │  │
│  │  V3__Add_Budget_Management.sql:                                     │  │
│  │  • CREATE TABLE budgets (1:Many with users)                         │  │
│  │  • CREATE TABLE budget_alerts (1:Many with budgets)                 │  │
│  │  • Indexes on user_id, period, triggered_at                         │  │
│  │                                                                      │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                ↓                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐  │
│  │                  Database (H2 or PostgreSQL)                        │  │
│  ├──────────────────────────────────────────────────────────────────────┤  │
│  │                                                                      │  │
│  │  Development: H2 in-memory (application-test.yml)                   │  │
│  │  Production: PostgreSQL (application.yml)                           │  │
│  │                                                                      │  │
│  │  Tables:                                                             │  │
│  │  • users (id PK, unique email, keycloak_id)                        │  │
│  │  • transactions (id PK, user_id FK)                                 │  │
│  │  • goals (id PK, user_id FK)                                        │  │
│  │  • user_preferences (id PK, user_id FK UNIQUE)                      │  │
│  │  • budgets (id PK, user_id FK, category, spent_amount)              │  │
│  │  • budget_alerts (id PK, budget_id FK, alert_type)                  │  │
│  │                                                                      │  │
│  │  Constraints:                                                        │  │
│  │  • Foreign keys with ON DELETE CASCADE                              │  │
│  │  • Unique indexes on email, user_preferences.user_id                │  │
│  │  • Composite indexes for filtering                                  │  │
│  │                                                                      │  │
│  └──────────────────────────────────────────────────────────────────────┘  │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
                                    ↓
┌─────────────────────────────────────────────────────────────────────────────┐
│                  EXTERNAL SERVICES (Future Integration)                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  • Keycloak (OAuth2 Provider) at http://localhost:8888                     │
│  • Email Service (for notifications)                                       │
│  • SMS Service (for alerts)                                                │
│  • AI Service (Phase 2.4) at http://localhost:5000                        │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Data Flow Examples

### Login Flow
```
User enters credentials → LoginPage
    ↓
Simulates OAuth2 exchange → stores in authStore + localStorage
    ↓
Zustand emits state update → updates useAuthStore hooks
    ↓
Protected routes check isAuthenticated → allow navigation
    ↓
Subsequent API calls include JWT in Authorization header
    ↓
Spring Security validates JWT with Keycloak keys
    ↓
AuthService.getOrCreateUser(jwt) → creates/retrieves User
    ↓
Controller proceeds with business logic
```

### Budget Creation Flow
```
User fills CreateBudgetForm → onClick handleSubmit()
    ↓
Sends POST /api/budgets + JWT token
    ↓
BudgetController.createBudget(jwt, request)
    ↓
AuthService.getOrCreateUser(jwt) → gets Long userId
    ↓
BudgetService.createBudget(userId, request)
    ↓
BudgetRepository.save(budget) → inserts into database
    ↓
Response: BudgetResponse with id, percentageSpent, etc.
    ↓
Frontend updates BudgetList with new budget
```

### Budget Alert Flow
```
User spends $400 (80% of $500 budget)
    ↓
Transaction service calls BudgetService.updateSpentAmount()
    ↓
checkAndCreateAlerts(budget, previousAmount) evaluates:
    - if previousSpent < 80% AND currentSpent >= 80% → WARNING alert
    - if previousSpent < 100% AND currentSpent >= 100% → EXCEEDED alert
    - if previousSpent >= 80% AND currentSpent < 80% → RECOVERED alert
    ↓
BudgetAlertRepository.save(alert) → persists alert
    ↓
Frontend displays visual indicators on BudgetCard
    ↓
User receives notification (Phase 2.8)
```

## Technology Stack

```
FRONTEND:
├─ React 18.3.1 (UI framework)
├─ TypeScript 5.6.2 (type safety)
├─ Vite 5.4.8 (build tool, dev server on :5173)
├─ React Router 6.28.0 (routing + protected routes)
├─ Zustand 4.5.4 (state management)
├─ Tailwind CSS 3.4.13 (styling)
├─ Framer Motion 11.0.25 (animations)
├─ Recharts 2.12.7 (charts/graphs)
└─ React Hot Toast 2.4.1 (notifications)

BACKEND:
├─ Java 21 (language, via --release flag)
├─ Spring Boot 3.3.2 (framework)
├─ Spring Security 6 (authentication/authorization)
├─ Spring Data JPA (ORM)
├─ Spring OAuth2 Resource Server (JWT validation)
├─ Lombok 1.18.34 (boilerplate reduction)
├─ Flyway 10+ (database migrations)
├─ Hibernate 6.5.2 (JPA implementation)
├─ PostgreSQL JDBC (production DB)
├─ H2 (development/test DB)
└─ Maven 3.9.12 (build tool)

DEPLOYMENT (Ready):
├─ Docker (containerization)
├─ Docker Compose (orchestration)
├─ GitHub Actions (CI/CD)
└─ Azure/AWS (cloud deployment)
```

## Key Design Patterns

1. **Separation of Concerns**: Controllers → Services → Repositories → Entities
2. **DTO Pattern**: Request/Response objects decouple API from internal models
3. **Authentication Injection**: @AuthenticationPrincipal Jwt jwt
4. **Protected Routes**: React ProtectedRoute wrapper + Spring Security filters
5. **State Persistence**: Zustand with localStorage for offline capability
6. **Database Migrations**: Flyway for version control of schema
7. **Alert Pattern**: Enumerated alert types (WARNING, EXCEEDED, RECOVERED)
8. **Lazy Loading**: JPA FetchType.LAZY on relationships
9. **Cascading Operations**: Automatic cleanup of alerts when budget deleted

---

**Last Updated**: February 3, 2026
**Version**: Phase 2.5 + 2.1
**Status**: Production-Ready
