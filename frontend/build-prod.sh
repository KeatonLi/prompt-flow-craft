#!/bin/bash

# 前端生产环境构建脚本
echo "正在构建前端生产版本..."
echo ""

# 安装依赖
echo "安装前端依赖..."
npm install

# 构建生产版本
echo "构建生产版本..."
npm run build

echo ""
echo "构建完成！"
echo "构建文件位于: dist/ 目录"
echo "可以使用 npm run preview 预览生产版本"
echo "或者将 dist/ 目录部署到静态文件服务器"