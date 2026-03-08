# AI Finance Advisor - Service Startup Script
# Default behavior starts only infrastructure containers for low-lag development

param(
    [ValidateSet("infra", "full")]
    [string]$Mode = "infra"
)

$ErrorActionPreference = "Stop"

# Color output helper
function Write-Status {
    param([string]$Message, [string]$Status = "INFO")
    $colors = @{
        "INFO"    = "Cyan"
        "SUCCESS" = "Green"
        "WARN"    = "Yellow"
        "ERROR"   = "Red"
    }
    Write-Host "[$(Get-Date -Format 'HH:mm:ss')] [$Status] $Message" -ForegroundColor $colors[$Status]
}

# Wait for Docker to be ready
function Wait-Docker {
    Write-Status "Waiting for Docker daemon to start..." "WARN"
    $maxAttempts = 60
    $attempt = 0
    
    while ($attempt -lt $maxAttempts) {
        try {
            $docker = docker version 2>&1
            if ($docker -match "Server:") {
                Write-Status "Docker is ready!" "SUCCESS"
                return $true
            }
        }
        catch {
            # Docker not ready yet
        }
        
        $attempt++
        Start-Sleep -Seconds 2
        
        if ($attempt % 10 -eq 0) {
            Write-Status "Still waiting for Docker ($attempt/$maxAttempts seconds)..." "WARN"
        }
    }
    
    Write-Status "Docker failed to start after $maxAttempts seconds" "ERROR"
    return $false
}

# Main execution
Write-Status "AI Finance Advisor Service Startup ($Mode mode)"
Write-Status "===================================" "INFO"

# Change to project directory
Set-Location $PSScriptRoot
Write-Status "Working directory: $(Get-Location)"

# Ensure Docker is running
if (-not (Wait-Docker)) {
    Write-Status "Cannot proceed without Docker. Please start Docker Desktop manually." "ERROR"
    exit 1
}

# Start Docker Compose services
Write-Status "Starting Docker Compose services..." "INFO"
try {
    if ($Mode -eq "infra") {
        docker compose up -d postgres keycloak redis mongo
        if ($LASTEXITCODE -ne 0) {
            throw "docker compose infra startup failed with exit code $LASTEXITCODE"
        }
        Write-Status "Infrastructure containers started (postgres, keycloak, redis, mongo)" "SUCCESS"
    }
    else {
        docker compose up -d
        if ($LASTEXITCODE -ne 0) {
            throw "docker compose full startup failed with exit code $LASTEXITCODE"
        }
        Write-Status "Full stack containers started" "SUCCESS"
    }
}
catch {
    Write-Status "Failed to start Docker Compose: $_" "ERROR"
    exit 1
}

# Wait for critical services
Write-Status "Waiting for services to be healthy..." "INFO"
Start-Sleep -Seconds 5

# Check Redis (required by gateway)
Write-Status "Checking Redis health..."
try {
    $redis = docker compose exec -T redis redis-cli ping 2>&1
    if ($redis -match "PONG") {
        Write-Status "Redis is healthy" "SUCCESS"
    }
}
catch {
    Write-Status "Redis health check: $_" "WARN"
}

# Check PostgreSQL (required by finance-service)
Write-Status "Checking PostgreSQL health..."
try {
    $db = docker compose exec -T postgres pg_isready -U postgres 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Status "PostgreSQL is healthy" "SUCCESS"
    }
}
catch {
    Write-Status "PostgreSQL health check: $_" "WARN"
}

Write-Status "Service startup complete." "SUCCESS"
Write-Status "Access points:"
Write-Status "  - Keycloak (Auth): http://localhost:8888"
Write-Status "  - Redis: localhost:6379"
Write-Status "  - PostgreSQL: localhost:5432"
Write-Status "  - MongoDB: localhost:27017"

if ($Mode -eq "full") {
    Write-Status "  - Finance Service (container): http://localhost:8081"
    Write-Status "  - AI Service (container): http://localhost:8000"
    Write-Status "  - Gateway (container): http://localhost:8080"
    Write-Status "  - Web (container): http://localhost"
}
else {
    Write-Status "Run apps locally for better performance:" "WARN"
    Write-Status "  1) services/finance-service -> mvn spring-boot:run -DskipTests"
    Write-Status "  2) services/ai-service -> .venv\\Scripts\\python.exe wsgi.py"
    Write-Status "  3) gateway -> mvn spring-boot:run -DskipTests"
    Write-Status "  4) web -> npm run dev"
}
