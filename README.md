# 🚀 Prompt Flow Craft

**通用大语言模型接入框架 - 智能 AI 提示词生成平台**

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.0+-4FC08D.svg)](https://vuejs.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> 🎯 **核心理念**: 提供统一的大语言模型接入框架，支持多种 AI 模型无缝切换！

## ✨ 项目特色

- 🔌 **多模型支持**: 统一接口支持多种大语言模型（OpenAI、Qwen、Claude 等）
- 🔄 **模型切换**: 配置化模型切换，无需修改代码
- 🎯 **精准定制**: 支持多维度参数配置，满足不同场景需求
- 📱 **响应式设计**: 完美适配桌面端和移动端
- 💾 **智能缓存**: MySQL 缓存机制，避免重复调用，节省费用
- 📊 **历史记录**: 完整的生成历史管理和查看功能
- 🚀 **高性能**: 前后端分离架构，支持独立部署和扩展
- 🛠️ **易扩展**: 插件化架构，轻松接入新的 AI 模型

## 🏗️ 技术架构

**前后端分离 + 插件化 AI 接入架构**

- **后端**: Spring Boot 3.2 + Spring WebFlux + JPA + MySQL
- **前端**: Vue 3 + Vite + Element Plus + Axios
- **AI 接入层**: 统一抽象接口，支持多种大语言模型
- **缓存层**: Redis/MySQL 智能缓存，提升响应速度
- **部署**: 支持云服务器自动化部署，容器化部署

## 📁 项目结构

```
prompt-flow-craft/
├── 📂 src/                           # 🔧 后端源码
│   └── main/
│       ├── java/com/promptflow/     # ☕ Java 核心代码
│       │   ├── controller/          # 🎮 REST API 控制器
│       │   ├── service/             # 🔧 业务逻辑服务
│       │   ├── entity/              # 📊 数据库实体
│       │   ├── dto/                 # 📦 数据传输对象
│       │   └── repository/          # 🗄️ 数据访问层
│       └── resources/               # ⚙️ 配置文件
├── 📂 frontend/                      # 🎨 前端项目
│   ├── src/                         # 🔧 Vue 源码
│   │   ├── views/                   # 📄 页面组件
│   │   ├── components/              # 🧩 通用组件
│   │   ├── api/                     # 🌐 API 接口
│   │   └── router/                  # 🛣️ 路由配置
│   ├── dist/                        # 📦 构建产物
│   └── package.json                 # 📋 前端依赖
├── 📄 pom.xml                        # 📋 后端依赖配置
├── 🚀 deploy.sh                      # 🔄 自动化部署脚本
└── 📖 README.md                      # 📚 项目文档
```

## 🚀 快速开始

### 📋 环境要求

- ☕ **Java**: 17 或更高版本
- 📦 **Maven**: 3.6 或更高版本
- 🟢 **Node.js**: 18 或更高版本
- 🗄️ **MySQL**: 8.0 或更高版本

### 🔧 后端启动

```bash
# 1. 克隆项目
git clone https://github.com/your-username/prompt-flow-craft.git
cd prompt-flow-craft

# 2. 配置数据库（参考下方数据库配置）

# 3. 启动后端服务
mvn spring-boot:run
```

✅ 后端服务启动成功：`http://localhost:8080`

### 🎨 前端启动

```bash
# 1. 进入前端目录
cd frontend

# 2. 安装依赖
npm install

# 3. 启动开发服务器
npm run dev

# 或使用便捷脚本
./start-dev.sh
```

✅ 前端服务启动成功：`http://localhost:3000`

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

## ⚙️ 环境配置

### 🤖 AI 模型配置

本框架支持多种大语言模型，采用统一的配置接口：

- 🔌 **多模型支持**: OpenAI GPT、阿里云 Qwen、Anthropic Claude 等
- 🔄 **热切换**: 运行时动态切换模型，无需重启服务
- 💰 **成本优化**: 根据需求选择最适合的模型和定价
- 🔧 **统一接口**: 兼容 OpenAI 标准，降低接入成本

#### 🔑 支持的模型提供商

| 提供商                | 模型示例            | 获取方式                                         |
| --------------------- | ------------------- | ------------------------------------------------ |
| 🤖**OpenAI**    | GPT-4, GPT-3.5      | [OpenAI Platform](https://platform.openai.com/)     |
| 🌟**阿里云**    | Qwen-Plus, Qwen-Max | [百炼平台](https://dashscope.console.aliyun.com/)   |
| 🧠**Anthropic** | Claude-3            | [Anthropic Console](https://console.anthropic.com/) |
| 🚀**其他**      | 自定义模型          | 实现统一接口即可接入                             |

### 🗄️ 数据库配置

#### 创建数据库

```sql
CREATE DATABASE prompt_flow_craft 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

#### 配置文件

编辑 `src/main/resources/application.yml`：

```yaml
# 🤖 AI 模型配置
ai:
  # 当前使用的模型提供商 (openai/qwen/claude/custom)
  provider: ${AI_PROVIDER:qwen}
  
  # OpenAI 配置
  openai:
    api-key: ${OPENAI_API_KEY:your-openai-key}
    base-url: https://api.openai.com/v1
    model: gpt-3.5-turbo
  
  # 阿里云 Qwen 配置
  qwen:
    api-key: ${QWEN_API_KEY:your-qwen-key}
    base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
    model: qwen-plus
  
  # Anthropic Claude 配置
  claude:
    api-key: ${CLAUDE_API_KEY:your-claude-key}
    base-url: https://api.anthropic.com/v1
    model: claude-3-sonnet-20240229
  
  # 通用参数
  common:
    temperature: 0.7                                   # 🌡️ 创造性参数
    max-tokens: 2000                                   # 📏 最大输出长度
    timeout: 30000                                     # ⏱️ 请求超时时间(ms)

# 🗄️ MySQL 数据库配置
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:prompt_flow_craft}?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:your_password}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update                               # 🔄 自动更新表结构
    show-sql: false                                  # 📝 SQL 日志
    database-platform: org.hibernate.dialect.MySQLDialect
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

## 📡 API 接口文档

### 🎯 核心接口

#### 生成提示词

```http
POST /api/generate-prompt
Content-Type: application/json
```

**请求参数：**

```json
{
  "task_description": "任务描述",      // 📝 必填：描述要完成的任务
  "target_audience": "目标受众",       // 👥 可选：目标用户群体
  "output_format": "输出格式",         // 📋 可选：期望的输出格式
  "constraints": "约束条件",           // ⚠️ 可选：限制条件
  "examples": "参考示例",             // 💡 可选：参考案例
  "tone": "语调风格",                 // 🎭 可选：语言风格
  "length": "内容长度"                // 📏 可选：内容长度要求
}
```

**响应示例：**

```json
{
  "success": true,
  "data": {
    "prompt": "生成的高质量提示词内容...",
    "cached": false,
    "model": "qwen-plus",
    "timestamp": "2024-01-01T12:00:00Z"
  },
  "message": "提示词生成成功"
}
```

#### 历史记录管理

```http
# 获取最近历史记录
GET /api/history/recent?limit=20

# 获取所有历史记录
GET /api/history/all

# 删除历史记录
DELETE /api/history/{id}
```

#### 系统监控

```http
# 健康检查
GET /api/health

# 缓存统计
GET /api/cache/stats
```

**缓存统计响应：**

```json
{
  "success": true,
  "data": {
    "total_cache_count": 156,
    "total_hit_count": 89,
    "hit_rate": "57.05%",
    "cache_size": "2.3MB"
  },
  "message": "缓存统计获取成功"
}
```

## 🛠️ 技术栈

### 🔧 后端技术栈

| 技术                          | 版本  | 说明             |
| ----------------------------- | ----- | ---------------- |
| ☕**Java**              | 17+   | 核心开发语言     |
| 🍃**Spring Boot**       | 3.2.0 | 主框架           |
| ⚡**Spring WebFlux**    | -     | 响应式编程       |
| 🗄️**Spring Data JPA** | -     | 数据访问层       |
| 🐬**MySQL**             | 8.0+  | 关系型数据库     |
| 📦**Maven**             | 3.6+  | 项目管理工具     |
| 🤖**多模型 API**        | -     | 统一 AI 模型接入 |

### 🎨 前端技术栈

| 技术                     | 版本 | 说明        |
| ------------------------ | ---- | ----------- |
| 💚**Vue.js**       | 3.0+ | 渐进式框架  |
| ⚡**Vite**         | 4.0+ | 构建工具    |
| 🎯**Element Plus** | -    | UI 组件库   |
| 🌐**Axios**        | -    | HTTP 客户端 |
| 🛣️**Vue Router** | 4.0+ | 路由管理    |
| 📱**响应式设计**   | -    | 移动端适配  |

## 🚀 生产部署

### 📦 后端部署

```bash
# 1. 打包应用
mvn clean package -DskipTests

# 2. 运行应用
java -jar target/prompt-flow-craft-1.0.0.jar

# 3. 后台运行（推荐）
nohup java -jar target/prompt-flow-craft-1.0.0.jar > app.log 2>&1 &
```

### 🌐 前端部署

```bash
# 1. 构建生产版本
cd frontend
npm run build

# 2. 部署到 Web 服务器
# 将 dist/ 目录内容部署到 Nginx/Apache 等
```

### ☁️ 云服务器一键部署

本项目提供自动化部署脚本，支持一键部署到云服务器：

```bash
# 使用部署脚本
./deploy.sh
```

**部署脚本功能：**

- 🔄 自动构建前后端项目
- 📤 上传文件到服务器
- 🗄️ 配置数据库环境
- 🚀 启动服务
- 🔍 健康检查

### 🌍 访问地址

部署完成后，可通过以下地址访问：

- 🎨 **前端应用**: `http://your_server_ip:3000`
- 🔧 **后端 API**: `http://your_server_ip:8080/api`
- 💚 **健康检查**: `http://your_server_ip:8080/api/health`

## 🔌 框架扩展

### 🛠️ 接入新的 AI 模型

本框架采用插件化设计，可以轻松接入新的大语言模型：

1. **实现统一接口**：继承 `AIModelProvider` 抽象类
2. **配置模型参数**：在 `application.yml` 中添加新模型配置
3. **注册模型服务**：通过 Spring 容器自动注册
4. **切换模型**：修改配置文件中的 `provider` 参数即可

```java
@Component
public class CustomModelProvider implements AIModelProvider {
  
    @Override
    public String generatePrompt(PromptRequest request) {
        // 实现自定义模型调用逻辑
        return callCustomModel(request);
    }
  
    @Override
    public String getProviderName() {
        return "custom";
    }
}
```

### 🔄 模型切换策略

- **配置切换**：修改配置文件，重启服务
- **动态切换**：通过管理接口实时切换（开发中）
- **负载均衡**：多模型并行，智能路由（规划中）
- **降级策略**：主模型失败时自动切换备用模型

## 🔧 开发指南

### 📋 开发规范

- 🎯 **代码风格**: 遵循 Java 和 Vue.js 官方规范
- 📝 **提交规范**: 使用 Conventional Commits 格式
- 🧪 **测试覆盖**: 核心业务逻辑需要单元测试
- 📚 **文档更新**: 重要功能需要更新文档

### 🔄 开发流程

1. **Fork 项目** → 创建功能分支
2. **开发功能** → 编写代码和测试
3. **提交代码** → 遵循提交规范
4. **创建 PR** → 描述变更内容
5. **代码审查** → 合并到主分支

### 🐛 问题反馈

如果遇到问题，请通过以下方式反馈：

- 🐛 [提交 Issue](https://github.com/your-username/prompt-flow-craft/issues)
- 💬 [讨论区](https://github.com/your-username/prompt-flow-craft/discussions)
- 📧 邮件联系：your-email@example.com

## 🤝 贡献指南

欢迎贡献代码！请查看 [CONTRIBUTING.md](CONTRIBUTING.md) 了解详细信息。

### 🌟 贡献者

感谢所有为项目做出贡献的开发者！

## 📄 许可证

本项目基于 [MIT License](LICENSE) 开源协议。

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给个 Star 支持一下！**

[![GitHub stars](https://img.shields.io/github/stars/your-username/prompt-flow-craft.svg?style=social&label=Star)](https://github.com/your-username/prompt-flow-craft)
[![GitHub forks](https://img.shields.io/github/forks/your-username/prompt-flow-craft.svg?style=social&label=Fork)](https://github.com/your-username/prompt-flow-craft/fork)

</div>
