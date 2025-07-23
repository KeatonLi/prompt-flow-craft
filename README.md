# Prompt Flow Craft

基于 Qwen（通义千问）模型的智能 AI 提示词生成平台

> 🎉 **最新更新**: 已从 Kimi API 切换到 Qwen API，**成本降低 80%-95%**！详见 [Qwen API 使用指南](QWEN_API_GUIDE.md)

## 项目架构

本项目采用前后端分离架构：

- **后端**: Spring Boot + Java + Maven + Qwen AI
- **前端**: Vue 3 + Vite + Element Plus
- **部署**: 前后端独立部署，通过 API 通信

## 项目结构

```
prompt-flow-craft/
├── src/                    # 后端源码
│   └── main/
│       ├── java/          # Java 源码
│       └── resources/     # 配置文件
├── frontend/              # 前端项目
│   ├── src/              # Vue 源码
│   ├── dist/             # 构建产物
│   └── package.json      # 前端依赖
├── pom.xml               # 后端依赖
└── README.md
```

## 快速开始

### 后端启动

1. 确保已安装 Java 17+ 和 Maven
2. 在项目根目录执行：

```bash
# 启动后端服务
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 前端启动

1. 确保已安装 Node.js 18+
2. 进入前端目录：

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 或者使用启动脚本
./start-dev.sh
```

前端服务将在 `http://localhost:3000` 启动

## 生产部署

### 后端部署

```bash
# 打包后端
mvn clean package

# 运行 JAR 文件
java -jar target/prompt-flow-craft-1.0.0.jar
```

### 前端部署

```bash
cd frontend

# 构建生产版本
npm run build

# 或者使用构建脚本
./build-prod.sh

# 将 dist/ 目录部署到静态文件服务器
```

## 环境配置

### API 配置

本项目使用 **Qwen（通义千问）API**，相比 Kimi API **成本降低 80%-95%**！

📖 **详细配置指南**: [Qwen API 使用指南](QWEN_API_GUIDE.md)

**快速开始**:
1. 访问 [阿里云百炼平台](https://dashscope.console.aliyun.com/)
2. 注册并获取 API Key
3. 新用户可获得免费额度（每个模型 100万 tokens）

### 后端配置

编辑 `src/main/resources/application.yml`：

```yaml
# Qwen API 配置 (通义千问)
qwen:
  api:
    # 阿里云百炼平台 API Key，请替换为你的实际 API Key
    key: your-qwen-api-key
    # 阿里云百炼平台 API 地址
    base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
    # 模型选择：qwen-turbo(最便宜) / qwen-plus(推荐) / qwen-max(最强) / qwen-long(超长文本)
    model: qwen-plus
    temperature: 0.7
    max-tokens: 2000

# MySQL 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/prompt_flow_craft?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### 数据库设置

本项目集成了 MySQL 缓存功能，可以显著减少 API 调用次数：

1. **安装 MySQL 8.0+**
2. **创建数据库**：
   ```sql
   CREATE DATABASE prompt_flow_craft CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. **初始化表结构**：
   ```bash
   mysql -u root -p prompt_flow_craft < src/main/resources/schema.sql
   ```

详细设置指南请参考：[DATABASE_SETUP.md](DATABASE_SETUP.md)

### 缓存功能特性

- ✅ **智能缓存**：相同参数的请求自动返回缓存结果
- ✅ **API 节省**：避免重复调用 KIMI API，节省费用
- ✅ **性能提升**：缓存命中时响应速度更快
- ✅ **统计监控**：提供缓存命中率等统计信息

### 前端配置

- 开发环境: `frontend/.env.development`
- 生产环境: `frontend/.env.production`

```env
VITE_API_BASE_URL=http://localhost:8080
```

## API 接口

### 生成提示词

```
POST /api/generate-prompt
Content-Type: application/json

{
  "task_description": "任务描述",
  "target_audience": "目标受众",
  "output_format": "输出格式",
  "constraints": "约束条件",
  "examples": "参考示例",
  "tone": "语调风格",
  "length": "内容长度"
}
```

### 健康检查

```
GET /api/health
```

### 缓存统计

```
GET /api/cache/stats
```

响应示例：
```json
{
  "success": true,
  "total_cache_count": 25,
  "total_hit_count": 48,
  "hit_rate": "65.22%",
  "message": "缓存统计获取成功"
}
```

## 技术栈

### 后端技术

- Spring Boot 3.2.0
- Spring WebFlux
- Spring Data JPA
- MySQL 8.0+
- Maven
- Kimi K2 API

### 前端技术

- Vue 3
- Vite
- Element Plus
- Axios
- Vue Router

## 云服务器部署

本项目支持通过 GitHub Actions 自动部署到云服务器，无需 Docker，直接使用 `java -jar` 和前端静态文件服务。

### 快速部署步骤

1. **服务器环境准备**
   ```bash
   # 在云服务器上运行
   wget https://raw.githubusercontent.com/your-username/prompt-flow-craft/main/server-setup.sh
   bash server-setup.sh
   ```

2. **配置 GitHub Secrets**
   在仓库 Settings > Secrets 中添加：
   - `SERVER_HOST`: 服务器 IP 地址
   - `SERVER_USER`: SSH 用户名
   - `SERVER_SSH_KEY`: SSH 私钥
   - `SERVER_PORT`: SSH 端口 (可选，默认 22)

3. **修改配置文件**
   - 更新 `frontend/.env.production` 中的服务器 IP
   - 配置 `application.yml` 中的数据库连接

4. **触发部署**
   - 推送代码到 `main` 分支
   - 或在 GitHub Actions 页面手动触发

### 访问地址
- 前端: `http://your_server_ip:3000`
- 后端 API: `http://your_server_ip:8080/api`
- 健康检查: `http://your_server_ip:8080/health`

详细部署说明请参考 [DEPLOYMENT.md](DEPLOYMENT.md)

## 开发说明

1. **CORS 配置**: 后端已配置 CORS 支持前端跨域请求
2. **环境变量**: 前端使用 Vite 环境变量管理不同环境的配置
3. **API 通信**: 前端通过 Axios 与后端 API 通信
4. **独立部署**: 前后端可以独立部署到不同的服务器

## 许可证

MIT License