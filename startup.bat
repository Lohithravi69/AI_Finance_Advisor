@echo off
REM AI Finance Advisor - Auto-startup batch file
REM Place this in %APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup to auto-run

REM Wait for Docker Desktop to initialize (30 seconds)
timeout /t 30 /nobreak

REM Change to project directory
cd /d "C:\Users\lohit\Desktop\AIFinanceAdvisor"

REM Start infrastructure containers only (low-lag mode)
powershell.exe -NoProfile -ExecutionPolicy Bypass -WindowStyle Minimized -Command "& '.\start-services.ps1' -Mode infra"

REM Exit
exit
