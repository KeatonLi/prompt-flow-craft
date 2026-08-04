#!/bin/sh
set -e

# 启动后端（内部端口 8080，nginx 反代目标）
java -jar /app/app.jar &

# 启动 nginx（前台主进程，容器退出信号交给它）
exec nginx -g 'daemon off;'
