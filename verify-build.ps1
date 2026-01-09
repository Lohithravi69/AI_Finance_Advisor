# AI Finance Advisor - Build Verification Script
# Run this to verify all services compile correctly

Write-Host "`n=== AI Finance Advisor - Build Verification ===" -ForegroundColor Cyan
Write-Host "This script will compile and verify all services.`n" -ForegroundColor Gray

# Check Maven installation
Write-Host "[1/4] Checking Maven installation..." -ForegroundColor Yellow
try {
    $mvnVersion = mvn --version 2>&1 | Select-String "Apache Maven"
    Write-Host "✅ Maven found: $mvnVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven not found. Please install Maven 3.9+ and add to PATH." -ForegroundColor Red
    exit 1
}

# Check Java installation
Write-Host "`n[2/4] Checking Java installation..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "✅ Java found: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Java not found. Please install Java 21+ and add to PATH." -ForegroundColor Red
    exit 1
}

# Compile Gateway
Write-Host "`n[3/4] Compiling Gateway..." -ForegroundColor Yellow
Push-Location "$PSScriptRoot\gateway"
try {
    $gatewayOutput = mvn clean compile -q 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Gateway compiled successfully" -ForegroundColor Green
    } else {
        Write-Host "❌ Gateway compilation failed:" -ForegroundColor Red
        Write-Host $gatewayOutput -ForegroundColor Red
        Pop-Location
        exit 1
    }
} catch {
    Write-Host "❌ Gateway compilation error: $_" -ForegroundColor Red
    Pop-Location
    exit 1
}
Pop-Location

# Compile Finance Service
Write-Host "`n[4/4] Compiling Finance Service..." -ForegroundColor Yellow
Push-Location "$PSScriptRoot\services\finance-service"
try {
    $financeOutput = mvn clean compile -q 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Finance Service compiled successfully" -ForegroundColor Green
    } else {
        Write-Host "❌ Finance Service compilation failed:" -ForegroundColor Red
        Write-Host $financeOutput -ForegroundColor Red
        Pop-Location
        exit 1
    }
} catch {
    Write-Host "❌ Finance Service compilation error: $_" -ForegroundColor Red
    Pop-Location
    exit 1
}
Pop-Location

# Success summary
Write-Host "`n=== ✅ All Services Compiled Successfully ===" -ForegroundColor Green
Write-Host "`nNext Steps:" -ForegroundColor Cyan
Write-Host "1. Start infrastructure: docker-compose up -d" -ForegroundColor Gray
Write-Host "2. Configure Keycloak (see KEYCLOAK_SETUP.md)" -ForegroundColor Gray
Write-Host "3. Start Gateway: cd gateway && mvn spring-boot:run" -ForegroundColor Gray
Write-Host "4. Start Finance Service: cd services/finance-service && mvn spring-boot:run" -ForegroundColor Gray
Write-Host "5. Start AI Service: cd services/ai-service && uvicorn app.main:app --reload" -ForegroundColor Gray
Write-Host "6. Start Web App: cd web && npm run dev" -ForegroundColor Gray
Write-Host "`nView README.md for detailed instructions.`n" -ForegroundColor Gray
