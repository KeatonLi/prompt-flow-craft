@echo off
REM Start Prompt Flow Craft with the correct environment variables
set MODEL_API_KEY=sk-REDACTED
set MODEL_NAME=deepseek-v4-flash
set MODEL_API_BASE_URL=https://api.deepseek.com/v1
set DB_HOST=111.231.107.210
set DB_PORT=13306
set DB_NAME=prompt
set DB_USERNAME=prompt
set DB_PASSWORD=promptSQL
set SERVER_PORT=8080

java -jar target\prompt-flow-craft-1.0.0.jar
pause
