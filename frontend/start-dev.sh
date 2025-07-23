#!/bin/bash

# 前端开发环境启动脚本
echo "正在启动前端开发服务器..."
echo "API 服务器地址: http://localhost:8080"
echo "前端服务器地址: http://localhost:3000"
echo ""
echo "请确保后端服务已启动在 8080 端口"
echo ""

# 安装依赖（如果需要）
if [ ! -d "node_modules" ]; then
    echo "安装前端依赖..."
    npm install
fi

# 启动开发服务器
npm run dev