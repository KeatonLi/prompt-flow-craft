# ============================================
# Prompt Flow Craft 后端镜像（微信云托管 / Docker 部署）
# 构建：在仓库根目录执行 docker build -f backend/Dockerfile .
# 微信云托管流水线：目标目录选仓库根，DockerFile 路径填 backend/Dockerfile，
#   服务端口填 80（与默认 SERVER_PORT 一致），健康检查用 / 或 /api/health
# ============================================

# ---- 构建阶段 ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B
COPY backend/src ./src
RUN mvn clean package -DskipTests -B

# ---- 运行阶段 ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/target/prompt-flow-craft-1.0.0.jar app.jar
EXPOSE 80
ENTRYPOINT ["java", "-jar", "app.jar"]
