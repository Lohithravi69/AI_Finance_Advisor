# Docker Auto-Startup Configuration

## Problem
Docker was not starting automatically after system login or when manually launched, requiring manual startup each session.

## Solution Implemented

### 1. **Docker Desktop Registry Auto-Start** ✅
- **Status**: Already enabled in Windows Registry at `HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Run`
- **Location**: `C:\Program Files\Docker\Docker\Docker Desktop.exe`
- **Effect**: Docker Desktop binary runs at user login

### 2. **Service Startup Script** ✅
- **File**: `C:\Users\lohit\Desktop\AIFinanceAdvisor\start-services.ps1`
- **Function**: 
  - Waits for Docker daemon to be ready (up to 60 seconds)
  - Starts docker-compose with all infrastructure services
  - Validates service health (PostgreSQL, Redis, etc.)
  - Displays access points after startup

### 3. **Batch Launcher** ✅
- **File**: `C:\Users\lohit\Desktop\AIFinanceAdvisor\startup.bat`
- **Function**:
  - Provides 30-second delay for Docker Desktop initialization
  - Launches PowerShell startup script
  - Runs minimized in background

### 4. **Windows Startup Shortcut** ✅
- **Location**: `C:\Users\lohit\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup\AIFinanceAdvisor-Services.lnk`
- **Trigger**: Automatically executes on user login (no admin required)
- **Action**: Runs `startup.bat`

## Service Startup Timeline
```
1. Windows Login
   ↓
2. Docker Desktop Auto-Starts (registry entry)
   ↓
3. Startup Shortcut Executes (from Startup folder)
   ↓
4. startup.bat Runs (30-second wait for Docker init)
   ↓
5. PowerShell Script Launches (start-services.ps1)
   ↓
6. Docker Daemon Health Check (waits up to 60 seconds)
   ↓
7. docker-compose up -d (starts all containers)
   ↓
8. Service Health Validation (PostgreSQL, Redis)
   ↓
✓ All services ready (PostgreSQL:5432, Redis:6379, Keycloak:8888, etc.)
```

## Accessing Services on Startup

Once auto-started, services will be available at:

| Service | URL | Port |
|---------|-----|------|
| Keycloak (Auth) | http://localhost:8888 | 8888 |
| PostgreSQL | localhost:5432 | 5432 |
| Redis | localhost:6379 | 6379 |
| MongoDB | localhost:27017 | 27017 |
| Finance Service (Docker) | http://localhost:8081 | 8081 |
| AI Service (Docker) | http://localhost:8000 | 8000 |
| Gateway (Docker) | http://localhost:8082 | 8082 |

## Running Services Locally (without Docker)

If you want to run services locally for development:

### Finance Service (Java/Spring Boot)
```bash
cd services/finance-service
mvn spring-boot:run -DskipTests
# Runs on port 8080
```

### Gateway (Java/Spring Boot)
```bash
cd gateway
mvn spring-boot:run -DskipTests
# Runs on port 8082
```

### AI Service (Python/Flask)
```bash
cd services/ai-service
python wsgi.py
# Runs on port 8001 (uses $AI_SERVICE_PORT environment variable)
```

### Frontend (React/Vite)
```bash
cd web
npm run dev
# Runs on port 5173
```

## Troubleshooting

### Services didn't start on login
1. Check if shortcut exists: `C:\Users\lohit\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup`
2. Verify startup.bat is executable and accessible
3. Check Docker Desktop is running: `docker ps`
4. Manually run: `powershell -NoProfile -ExecutionPolicy Bypass -File "C:\Users\lohit\Desktop\AIFinanceAdvisor\start-services.ps1"`

### Docker still not starting automatically
1. Ensure Docker Desktop is installed: `C:\Program Files\Docker\Docker\Docker Desktop.exe`
2. Open Docker Desktop settings → General → Check "Start Docker Desktop on login"
3. Verify Windows user account has permission to run startup scripts

### Services taking too long to start
- Increase delay in `startup.bat` (currently 30 seconds)
- Services may take 30-60 seconds to fully initialize after docker-compose up

### Container conflicts on port already in use
```bash
docker ps -a  # List all containers
docker rm [container_id]  # Remove conflicting container
docker-compose up -d  # Restart
```

## Manual Startup (without auto-start)
```bash
# Manual test of startup script
cd "C:\Users\lohit\Desktop\AIFinanceAdvisor"
powershell -NoProfile -ExecutionPolicy Bypass -File ".\start-services.ps1"
```

---

**Last Updated**: February 24, 2026
**Status**: ✅ All Docker services configured for automatic startup
