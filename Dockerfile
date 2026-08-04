# ============================================
# Prompt Flow Craft 前后端一体镜像（微信云托管 / Docker 部署）
#
# 架构：单容器内 nginx(80) 托管前端静态文件 + 反代 /api 到容器内后端(8080)
#   - 外部只暴露 80（nginx），后端监听内部端口 8080 不对外
#   - SSE 流式输出经 nginx 关闭 buffering 直通
#
# 构建：在仓库根目录执行 docker build -t prompt-flow-craft .
# 微信云托管流水线：目标目录=仓库根，Dockerfile=Dockerfile（默认），服务端口=80，
#   健康检查用 / 或 /api/health
# ============================================

# ---- 阶段1：构建后端 jar ----
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /build/backend
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY backend/src ./src
RUN mvn clean package -DskipTests -B

# ---- 阶段2：构建前端 dist ----
FROM node:20-alpine AS frontend-build
WORKDIR /build/frontend
COPY frontend/package*.json ./
RUN npm ci --no-audit --no-fund || npm install --no-audit --no-fund
COPY frontend/ ./
# 生产构建：VITE_API_BASE_URL=/api（同域相对路径，由 nginx 反代到后端）
RUN npm run build

# ---- 阶段3：运行时（nginx + JRE）----
FROM nginx:1.27-alpine

# 安装 Java 运行时（后端进程需要）
RUN apk add --no-cache openjdk17-jre-headless

# 前端静态文件
COPY --from=frontend-build /build/frontend/dist /usr/share/nginx/html
# 后端 jar
COPY --from=backend-build /build/backend/target/prompt-flow-craft-1.0.0.jar /app/app.jar
# nginx 配置（托管前端 + 反代 /api）
COPY nginx.conf /etc/nginx/conf.d/default.conf
# 启动脚本：先后端（后台）再 nginx（前台主进程）
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# 后端内部端口固定为 8080（nginx 反代目标），勿用环境变量覆盖
ENV SERVER_PORT=8080

EXPOSE 80
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
