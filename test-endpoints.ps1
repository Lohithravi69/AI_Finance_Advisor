#!/usr/bin/env pwsh

# Test REST Endpoints for Finance Service

$baseUrl = "http://localhost:8081/api"
$token = "test-user"
$headers = @{"Authorization"="Bearer $token"}

Write-Host "========== Finance Service REST Endpoints Test ==========" -ForegroundColor Cyan
Write-Host ""

# Test 1: Create a transaction
Write-Host "[TEST 1] POST /api/transactions - Create Transaction" -ForegroundColor Yellow
$transactionBody = @{
    type = "INCOME"
    amount = 5000.00
    description = "Monthly salary"
    transactionDate = "2026-01-25"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$baseUrl/transactions" -Method POST -Headers $headers -Body $transactionBody -ContentType "application/json"
    Write-Host "✓ Status: $($response.StatusCode)" -ForegroundColor Green
    $response.Content | ConvertFrom-Json | ConvertTo-Json | Write-Host
    $createdTransactionId = ($response.Content | ConvertFrom-Json).id
} catch {
    Write-Host "✗ Error: $_" -ForegroundColor Red
}

Write-Host ""

# Test 2: List transactions
Write-Host "[TEST 2] GET /api/transactions - List Transactions" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/transactions?limit=10" -Method GET -Headers $headers
    Write-Host "✓ Status: $($response.StatusCode)" -ForegroundColor Green
    $response.Content | ConvertFrom-Json | ConvertTo-Json | Write-Host
} catch {
    Write-Host "✗ Error: $_" -ForegroundColor Red
}

Write-Host ""

# Test 3: Create a goal
Write-Host "[TEST 3] POST /api/goals - Create Goal" -ForegroundColor Yellow
$goalBody = @{
    title = "Save for vacation"
    description = "Summer vacation fund"
    targetAmount = 10000.00
    currentAmount = 2000.00
    deadline = "2026-07-01"
} | ConvertTo-Json

try {
    $response = Invoke-WebRequest -Uri "$baseUrl/goals" -Method POST -Headers $headers -Body $goalBody -ContentType "application/json"
    Write-Host "✓ Status: $($response.StatusCode)" -ForegroundColor Green
    $response.Content | ConvertFrom-Json | ConvertTo-Json | Write-Host
    $createdGoalId = ($response.Content | ConvertFrom-Json).id
} catch {
    Write-Host "✗ Error: $_" -ForegroundColor Red
}

Write-Host ""

# Test 4: List goals
Write-Host "[TEST 4] GET /api/goals - List Goals" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/goals" -Method GET -Headers $headers
    Write-Host "✓ Status: $($response.StatusCode)" -ForegroundColor Green
    $response.Content | ConvertFrom-Json | ConvertTo-Json | Write-Host
} catch {
    Write-Host "✗ Error: $_" -ForegroundColor Red
}

Write-Host ""

# Test 5: Get monthly summary
Write-Host "[TEST 5] GET /api/summary - Monthly Summary" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/summary?month=2026-01" -Method GET -Headers $headers
    Write-Host "✓ Status: $($response.StatusCode)" -ForegroundColor Green
    $response.Content | ConvertFrom-Json | ConvertTo-Json | Write-Host
} catch {
    Write-Host "✗ Error: $_" -ForegroundColor Red
}

Write-Host ""

# Test 6: Get yearly summary
Write-Host "[TEST 6] GET /api/summary/yearly - Yearly Summary" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/summary/yearly?year=2026" -Method GET -Headers $headers
    Write-Host "✓ Status: $($response.StatusCode)" -ForegroundColor Green
    $response.Content | ConvertFrom-Json | ConvertTo-Json | Write-Host
} catch {
    Write-Host "✗ Error: $_" -ForegroundColor Red
}

Write-Host ""
Write-Host "========== Test Completed ==========" -ForegroundColor Cyan
