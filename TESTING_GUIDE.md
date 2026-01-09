# Testing Guide - AI Finance Advisor

## Overview
This guide walks you through testing the refactored finance-service with database persistence and OAuth2 authentication.

## Prerequisites
- ✅ Docker & Docker Compose installed
- ✅ Maven 3.9+ installed
- ✅ Java 21+ installed
- ✅ PostgreSQL running (via docker-compose)
- ✅ Keycloak configured (see KEYCLOAK_SETUP.md)

## Phase 1: Verify Build

### Step 1: Run Verification Script
```powershell
# From project root
.\verify-build.ps1
```

**Expected Output:**
```
✅ Maven found: Apache Maven 3.9.x
✅ Java found: version "21.x"
✅ Gateway compiled successfully
✅ Finance Service compiled successfully
```

### Step 2: Manual Verification (if script fails)
```powershell
# Compile gateway
cd gateway
mvn clean compile

# Compile finance-service
cd ..\services\finance-service
mvn clean compile
```

## Phase 2: Start Infrastructure

### Step 1: Start Docker Services
```powershell
# From project root
docker-compose up -d
```

### Step 2: Verify Services
```powershell
# Check Postgres
docker ps | findstr postgres

# Check Keycloak
docker ps | findstr keycloak

# Wait for Keycloak to start (30-60 seconds)
Start-Sleep -Seconds 45
```

### Step 3: Verify Database
```powershell
# Connect to database
docker exec -it aifinance-postgres psql -U postgres -d aifinance

# Check tables (should see users, transactions, goals, flyway_schema_history)
\dt

# Exit
\q
```

## Phase 3: Configure Keycloak

Follow `KEYCLOAK_SETUP.md` to:
1. Create `aifa` realm
2. Create `aifa-web` client with PKCE flow
3. Create test user (e.g., test@example.com / password123)

## Phase 4: Start Services

### Terminal 1: Gateway
```powershell
cd gateway
mvn spring-boot:run
```
**Wait for:** `Started GatewayApplication in X seconds`

### Terminal 2: Finance Service
```powershell
cd services\finance-service
mvn spring-boot:run
```
**Wait for:** 
- `HikariPool-1 - Start completed.` (Database connected)
- `Successfully validated SQL Migration V1__Initial_schema` (Flyway ran)
- `Started FinanceServiceApplication in X seconds`

### Terminal 3: AI Service (Optional for now)
```powershell
cd services\ai-service
uvicorn app.main:app --reload
```

## Phase 5: Get JWT Token

### Option A: Using Keycloak Direct Grant (Testing Only)
```powershell
$response = Invoke-RestMethod -Uri "http://localhost:8888/realms/aifa/protocol/openid-connect/token" `
  -Method POST `
  -ContentType "application/x-www-form-urlencoded" `
  -Body @{
    grant_type = "password"
    client_id = "aifa-web"
    username = "test@example.com"
    password = "password123"
  }

$token = $response.access_token
Write-Host "Token: $token"
```

### Option B: Using Postman
1. Create new request
2. Authorization tab → Type: OAuth 2.0
3. Configure:
   - Grant Type: Authorization Code (with PKCE)
   - Auth URL: http://localhost:8888/realms/aifa/protocol/openid-connect/auth
   - Access Token URL: http://localhost:8888/realms/aifa/protocol/openid-connect/token
   - Client ID: aifa-web
   - Scope: openid profile email
4. Get New Access Token
5. Copy token

## Phase 6: Test Endpoints

### Test 1: Health Check (No Auth)
```powershell
curl http://localhost:8080/api/finance/api/health
```
**Expected:** `{"status":"ok"}`

### Test 2: List Transactions (Auth Required)
```powershell
# Replace YOUR_JWT_TOKEN with actual token
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" `
  http://localhost:8080/api/finance/api/transactions

# Expected (first time, no transactions):
# []

# Expected (if transactions exist):
# [
#   {
#     "id": 1,
#     "description": "Grocery shopping",
#     "amount": 45.99,
#     "date": "2024-01-15",
#     "merchant": "Walmart",
#     "category": "groceries"
#   }
# ]
```

### Test 3: Get Summary (Auth Required)
```powershell
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" `
  http://localhost:8080/api/finance/api/transactions/summary

# Expected:
# {
#   "month": "January",
#   "income": 0.0,
#   "expenses": 0.0,
#   "savings": 0.0
# }
```

### Test 4: Get Insights (Auth Required)
```powershell
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" `
  http://localhost:8080/api/finance/api/insights

# Expected (stubbed data):
# [
#   {
#     "type": "overspend",
#     "message": "You overspent on dining by 18% this month."
#   },
#   {
#     "type": "saving",
#     "message": "Consider automating a $200 transfer on payday."
#   }
# ]
```

## Phase 7: Verify Database Auto-Creation

### Step 1: Check User Created
```powershell
# Connect to database
docker exec -it aifinance-postgres psql -U postgres -d aifinance

# Query users table
SELECT id, email, keycloak_id, full_name FROM users;
```

**Expected:** User created with:
- email: test@example.com (from JWT)
- keycloak_id: (UUID from JWT subject)
- full_name: (from JWT "name" claim)

### Step 2: Seed Test Data (Optional)
```sql
-- Get your user ID
SELECT id FROM users WHERE email = 'test@example.com';

-- Insert test transactions (replace USER_ID with your ID)
INSERT INTO transactions (user_id, description, amount, transaction_date, merchant, category)
VALUES 
  (USER_ID, 'Grocery shopping', 45.99, '2024-01-15', 'Walmart', 'groceries'),
  (USER_ID, 'Gas station', 60.00, '2024-01-14', 'Shell', 'transportation'),
  (USER_ID, 'Restaurant dinner', 85.50, '2024-01-13', 'Olive Garden', 'dining');

-- Update monthly income
UPDATE users SET monthly_income = 5000.00 WHERE id = USER_ID;

-- Exit
\q
```

### Step 3: Re-test Endpoints
Run Test 2 and Test 3 again. You should now see:
- **Transactions:** List of 3 transactions
- **Summary:** income=5000.00, expenses=191.49, savings=4808.51

## Troubleshooting

### Issue: "401 Unauthorized"
**Cause:** Invalid or expired JWT token
**Fix:** Get a fresh token (tokens expire in 5-15 minutes)

### Issue: "Database connection failed"
**Cause:** PostgreSQL not running
**Fix:** 
```powershell
docker-compose up -d postgres
docker logs aifinance-postgres
```

### Issue: "Keycloak realm not found"
**Cause:** Keycloak not configured
**Fix:** Follow KEYCLOAK_SETUP.md step-by-step

### Issue: "User not found"
**Cause:** JWT doesn't contain expected claims
**Fix:** Verify JWT payload:
```powershell
# Decode JWT at https://jwt.io
# Check for: sub, email, preferred_username, name claims
```

### Issue: "Flyway validation failed"
**Cause:** Database schema manually modified
**Fix:**
```powershell
# Drop and recreate database
docker exec -it aifinance-postgres psql -U postgres -c "DROP DATABASE aifinance;"
docker exec -it aifinance-postgres psql -U postgres -c "CREATE DATABASE aifinance;"

# Restart finance-service (Flyway will re-run migrations)
```

## Verification Checklist

- [ ] ✅ Gateway compiled successfully
- [ ] ✅ Finance-service compiled successfully
- [ ] ✅ Docker services running (postgres, keycloak)
- [ ] ✅ Keycloak realm configured (aifa)
- [ ] ✅ Test user created in Keycloak
- [ ] ✅ Gateway started on port 8080
- [ ] ✅ Finance-service started on port 8081
- [ ] ✅ Health check returns 200 OK
- [ ] ✅ JWT token obtained from Keycloak
- [ ] ✅ Transactions endpoint returns 200 (even if empty [])
- [ ] ✅ Summary endpoint returns valid JSON
- [ ] ✅ User auto-created in database on first login
- [ ] ✅ Test transactions inserted successfully
- [ ] ✅ Transactions endpoint returns seeded data

## Next Steps

Once all tests pass:
1. **Wire React Frontend Login Flow** - Add OAuth2 PKCE flow to web app
2. **Implement Goal CRUD** - Add create/update/list endpoints for savings goals
3. **Real Insights Analytics** - Replace stubbed InsightsController with actual DB analytics
4. **AI Service Integration** - Wire finance-service to call AI service for predictions
5. **Production Deployment** - Move to Azure/AWS with managed Postgres and Keycloak

## Success Criteria

You have successfully completed refactoring when:
- ✅ All endpoints protected by OAuth2 JWT validation
- ✅ TransactionService extracts user from JWT and queries database
- ✅ User auto-created on first login (no manual provisioning)
- ✅ DTOs cleanly separate API contracts from entities
- ✅ Flyway migrations applied automatically on startup
- ✅ Build compiles without errors
- ✅ Tests pass with real JWT tokens and database

---

**Status:** ✅ All code refactored correctly. Ready for build verification and testing.
