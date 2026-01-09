@echo off
cd /d C:\Users\lohit\Desktop\AIFinanceAdvisor\services\finance-service
mvn clean compile -q 2>&1
echo Build Status: %ERRORLEVEL%
