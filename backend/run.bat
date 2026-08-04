@echo off
REM Start Prompt Flow Craft with the correct environment variables
REM 本地运行脚本：请将下方占位符替换为真实值后再执行（勿提交真实密钥到仓库）
set LLM_AUTH_TOKEN=sk-请填入你的LLM密钥
set LLM_MODEL=DeepSeek-V4-Flash
set LLM_BASE_URL=https://api.deepseek.com
set DB_HOST=请填入数据库地址
set DB_PORT=3306
set DB_NAME=alphapick
set DB_USERNAME=root
set DB_PASSWORD=请填入数据库密码
set SERVER_PORT=8080

java -jar target\prompt-flow-craft-1.0.0.jar
pause
