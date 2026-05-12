# Prompt Flow Craft

### 企业级 AI 提示词工程平台

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4+-4FC08D.svg)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0+-3178C6.svg)](https://www.typescriptlang.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-4479A1.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

[**English**](README.md) | 中文

---

## 🎯 产品简介

**Prompt Flow Craft** 是一款面向企业级应用的 AI 提示词工程平台，基于 **双引擎架构（Agent + Skill）** 设计理念，支持 AI 智能体的快速构建与发布。

平台首创性地将提示词工程分解为两大核心模块：**Agent 引擎**负责角色塑造与对话决策，**Skill 引擎**负责工具调用与能力扩展，二者协同构建完整的 AI 应用闭环。

---

## 🏛️ 系统架构

### 六层微服务架构

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER (展示层)                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │
│  │   Home      │  │  Generate   │  │  Templates   │  │ Statistics  │  │
│  │   首页      │  │  提示词生成  │  │   模板中心   │  │   数据大盘  │  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘  │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │ HTTP / SSE
┌────────────────────────────────▼────────────────────────────────────────┐
│                       APPLICATION LAYER (应用层)                          │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                     RESTful API Gateway                             │   │
│  │   /api/prompt/generate  │  /api/history  │  /api/share  │ /api/* │   │
│  └──────────────────────────────────────────────────────────────────┘   │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │ Interface
┌────────────────────────────────▼────────────────────────────────────────┐
│                        BUSINESS LAYER (业务层)                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌───────────┐  │
│  │   Agent      │  │    Skill     │  │   History    │  │ Community │  │
│  │   Service    │  │   Service    │  │   Service    │  │  Service  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  └───────────┘  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌───────────┐  │
│  │  Classify    │  │    Auth      │  │  Analytics   │  │  LLM     │  │
│  │  Service     │  │   Service    │  │   Service    │  │  Client  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  └───────────┘  │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │ Interface
┌────────────────────────────────▼────────────────────────────────────────┐
│                          DOMAIN LAYER (领域层)                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌───────────┐  │
│  │   Agent      │  │    Skill     │  │    User      │  │ Community │  │
│  │   Entity    │  │   Entity     │  │   Entity     │  │  Entity   │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  └───────────┘  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌───────────┐  │
│  │   Category   │  │     Tag      │  │   Comment    │  │  Rating   │  │
│  │   Entity    │  │   Entity     │  │   Entity     │  │  Entity   │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  └───────────┘  │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │ Interface
┌────────────────────────────────▼────────────────────────────────────────┐
│                      INFRASTRUCTURE LAYER (基础设施层)                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌───────────┐  │
│  │  Repository  │  │     JPA      │  │    Cache     │  │    SSO    │  │
│  │    Layer     │  │   Hibernate  │  │   Redis     │  │  Gateway  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  └───────────┘  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌───────────┐  │
│  │  Database    │  │    Message   │  │    File     │  │   Log     │  │
│  │   MySQL 8   │  │    Queue    │  │   Storage   │  │  Monitor  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  └───────────┘  │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │
┌────────────────────────────────▼────────────────────────────────────────┐
│                       EXTERNAL SERVICES (外部服务)                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                  │
│  │   LLM API    │  │  DashScope  │  │   Network   │                  │
│  │  (Qwen/A4)   │  │  (Alibaba)  │  │  HTTP/WS   │                  │
│  └──────────────┘  └──────────────┘  └──────────────┘                  │
└─────────────────────────────────────────────────────────────────────────┘
```

### 前端三层 SPA 架构

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         VIEW LAYER (视图层)                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │
│  │    Home     │  │   Generate  │  │  Templates  │  │ Statistics  │  │
│  │   页面组件   │  │  页面组件    │  │   页面组件   │  │   页面组件   │  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │
│  │ HistoryCard │  │ ShareCard   │  │ DetailModal│  │ PublishModal│  │
│  │   卡片组件   │  │   卡片组件   │  │   弹窗组件   │  │   弹窗组件   │  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘  │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │ Props / Events
┌────────────────────────────────▼────────────────────────────────────────┐
│                    COMPOSABLE LAYER (可组合层)                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                  │
│  │  useAdmin   │  │  usePrompt   │  │  useHistory │                  │
│  │  管理状态    │  │  提示词状态   │  │   历史状态   │                  │
│  └──────────────┘  └──────────────┘  └──────────────┘                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                  │
│  │  useShare   │  │   useToast   │  │   useModal  │                  │
│  │   分享状态   │  │   Toast通知  │  │   弹窗管理   │                  │
│  └──────────────┘  └──────────────┘  └──────────────┘                  │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │ Store / API
┌────────────────────────────────▼────────────────────────────────────────┐
│                       SERVICE LAYER (服务层)                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                  │
│  │  promptApi  │  │  historyApi  │  │  shareApi   │                  │
│  │  提示词接口   │  │   历史接口    │  │   分享接口   │                  │
│  └──────────────┘  └──────────────┘  └──────────────┘                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                  │
│  │   request   │  │   useAdmin   │  │    stores   │                  │
│  │  HTTP封装   │  │  管理状态管理  │  │   Pinia状态  │                  │
│  └──────────────┘  └──────────────┘  └──────────────┘                  │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## ✨ 核心功能

### 🤖 Agent Engine - 智能体引擎

Agent 是具有特定角色定位和能力的 AI 智能体，采用 **角色建模 + 能力边界 + 行为规范** 三位一体设计：

| 模块 | 说明 | 技术实现 |
|------|------|----------|
| 角色定位 | Agent 核心身份与专业领域 | Role Description Embedding |
| 核心能力 | Agent 可完成的 10+ 种任务类型 | Capability Vector |
| 行为规范 | Agent 决策边界与约束条件 | Behavior Constraint Model |
| 对话风格 | 专业/友好/简洁/创意 等维度 | Style Profile Mapping |

**Agent 生成流程：**
```
用户输入 → 角色建模 → 能力编排 → 约束注入 → LLM推理 → 流式输出 → 自动保存
```

### ⚡ Skill Engine - 技能引擎

Skill 是 Agent 可调用的外部工具，支持 **四种技能类型**：

| 类型 | 协议 | 应用场景 |
|------|------|----------|
| API | HTTP/REST | 天气查询、翻译服务、支付接口 |
| Function | JSON RPC | 数据处理、格式转换、本地计算 |
| Webhook | HTTP Callback | 事件通知、消息推送、回传机制 |
| Data | SQL/NoSQL | 数据库查询、文件读取、缓存访问 |

**Skill 定义结构：**
```json
{
  "name": "get_weather",
  "type": "api",
  "method": "GET",
  "endpoint": "https://api.weather.example.com/v1/current",
  "parameters": {
    "city": { "type": "string", "required": true }
  },
  "authentication": { "type": "bearer", "token": "xxx" },
  "rateLimit": { "requests": 100, "period": "minute" }
}
```

### 🌐 Community Hub - 社区中心

基于 **社交图谱** 构建的提示词共享生态：

- **发布系统**: 一键分享 Agent/Skill 到社区广场
- **互动系统**: 点赞、收藏、评论、评分
- **推荐系统**: 基于热度的排序 + 个性化推荐
- **治理系统**: 管理员模式、社区举报、去重机制

### 🧠 AI Classification - 智能分类

采用 **规则引擎 + LLM 混合策略** 实现自动分类：

```
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│  Keyword Match  │  →   │  LLM Inference  │  →   │  Result Merge   │
│   规则快速匹配   │      │   LLM 智能推断   │      │   结果融合校验   │
└─────────────────┘      └─────────────────┘      └─────────────────┘
         ↓                                               ↓
   置信度 > 0.7                                最终分类 + 标签输出
```

**8 大预设分类：**
办公效率 | 内容创作 | 代码开发 | 教育培训 | 视频媒体 | 数据分析 | 设计创意 | 智能对话

### 🏷️ Auto Tagging - 自动打标

保存时自动调用 LLM 生成 **精确 5 标签**，采用：

- **语义分析**: 理解提示词核心主题
- **领域识别**: 匹配技术/业务/创意标签
- **热度追踪**: 标签使用频率统计

---

## 🗄️ 数据库架构

### ER 关系图

```
┌──────────────────┐       ┌──────────────────┐       ┌──────────────────┐
│ prompt_category   │       │ prompt_resource  │       │   shared_prompt  │
│ ─────────────────│       │ ─────────────────│       │ ─────────────────│
│ id (PK)          │       │ id (PK)         │       │ id (PK)         │
│ name             │◄──────│ category_id (FK) │       │ prompt_type     │
│ icon             │       │ prompt_type      │       │ author_nickname │
│ color            │       │ name             │       │ description      │
│ description      │       │ generated_prompt │       │ prompt_content  │
│ sort_order       │       │ like_count       │       │ like_count       │
│ is_system        │       │ view_count       │       │ view_count       │
└──────────────────┘       │ created_at       │       │ created_at       │
                           └────────┬─────────┘       └──────────────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    │               │               │
                    ▼               ▼               ▼
        ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
        │ prompt_tag       │ │prompt_tag_relation│ │    likes         │
        │ ─────────────────│ │ ─────────────────│ │ ─────────────────│
        │ id (PK)         │ │ prompt_id (FK)  │ │ user_id          │
        │ name            │◄│ tag_id (FK)     │ │ prompt_id (FK)   │
        │ color           │ │                 │ │ created_at        │
        │ usage_count     │ └──────────────────┘ └──────────────────┘
        │ is_system       │
        └──────────────────┘
```

### 统一存储策略

所有类型的提示词（Agent/Skill/Generic）统一存储在 `prompt_resource` 表，通过 `prompt_type` 字段区分：

```sql
-- 统一表结构，支持多类型
CREATE TABLE prompt_resource (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    prompt_type     VARCHAR(20) NOT NULL,      -- 'agent' | 'skill'
    name            VARCHAR(100),
    -- Agent 特有字段
    role_description TEXT,
    capabilities    TEXT,
    behaviors       TEXT,
    communication_style VARCHAR(50),
    -- Skill 特有字段
    skill_type      VARCHAR(20),
    method          VARCHAR(10),
    endpoint        TEXT,
    parameters      JSON,
    -- 通用字段
    generated_prompt TEXT NOT NULL,
    prompt_summary  VARCHAR(500),
    like_count      INT DEFAULT 0,
    view_count      INT DEFAULT 0,
    -- 分类与标签
    category_id    BIGINT,
    is_auto_tagged  BOOLEAN DEFAULT FALSE,
    ai_tags         JSON,
    -- 元数据
    created_at      DATETIME,
    updated_at      DATETIME,
    INDEX idx_prompt_type (prompt_type),
    INDEX idx_category (category_id),
    INDEX idx_created (created_at)
);
```

---

## 📦 技术栈

### 后端技术栈

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 运行时 | Java | 17+ | LTS 版本，特性支持 |
| 框架 | Spring Boot | 3.2.0 | 约定优于配置 |
| ORM | Hibernate/JPA | 6.4 | 对象关系映射 |
| 数据库 | MySQL | 8.0+ | 主从架构支持 |
| 连接池 | HikariCP | - | 高性能连接管理 |
| 验证 | Hibernate Validator | - | Bean Validation |
| API | Spring MVC | - | RESTful 风格 |
| 日志 | SLF4J + Logback | - | 可配置日志 |
| 构建 | Maven | 3.8+ | 依赖管理 |

### 前端技术栈

| 层级 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 框架 | Vue | 3.4+ | Composition API |
| 语言 | TypeScript | 5.0+ | 类型安全 |
| 构建 | Vite | 5.0+ | 极速开发体验 |
| 路由 | Vue Router | 4.0+ | SPA 路由管理 |
| 状态 | Pinia | 2.1+ | Composition Store |
| UI | Element Plus | 2.4+ | 企业级组件库 |
| 样式 | Tailwind CSS | 3.0+ | 原子化 CSS |
| 图表 | ECharts | 5.0+ | 数据可视化 |
| 渲染 | Markdown-it | - | Markdown 解析 |
| 代码高亮 | highlight.js | - | 语法高亮 |

---

## 🚀 快速开始

### 环境要求

| 环境 | 版本 | 说明 |
|------|------|------|
| Node.js | 18+ | 前端运行时 |
| Java | 17+ | 后端运行时 |
| MySQL | 8.0+ | 数据库服务 |
| Maven | 3.8+ | 后端构建 |

### 部署步骤

```bash
# 1. 克隆项目
git clone https://github.com/KeatonLi/prompt-flow-craft.git
cd prompt-flow-craft

# 2. 数据库初始化
mysql -u root -p < scripts/init.sql

# 3. 配置环境变量
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=prompt_flow
export DB_USERNAME=root
export DB_PASSWORD=your_password
export MODEL_API_KEY=your_api_key

# 4. 构建后端
cd backend
mvn clean package -DskipTests

# 5. 启动后端
java -jar target/prompt-flow-craft-1.0.0.jar

# 6. 构建前端
cd ../frontend
npm install
npm run build

# 7. 部署静态文件
# 将 dist 目录部署到 Nginx
```

### Docker 部署

```bash
# 一键启动完整服务
docker-compose up -d
```

---

## 📡 API 参考

### 提示词生成

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/prompt/generate/agent` | POST | 生成 Agent 提示词 |
| `/api/prompt/generate/skill` | POST | 生成 Skill 提示词 |
| `/api/prompt/generate/agent/stream` | POST | **流式** 生成 Agent |
| `/api/prompt/generate/skill/stream` | POST | **流式** 生成 Skill |

### 历史管理

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/history/agent` | POST | 保存 Agent |
| `/api/history/skill` | POST | 保存 Skill |
| `/api/history/page` | POST | 分页查询 |
| `/api/history/{id}` | GET | 详情查询 |
| `/api/history/{id}/like` | POST | 点赞 |
| `/api/history/{id}/classify` | POST | AI 分类 |

### 社区功能

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/share` | GET | 分享列表 |
| `/api/share/recent` | GET | 最新分享 |
| `/api/share/publish` | POST | 发布分享 |
| `/api/share/{id}/like` | POST | 点赞 |
| `/api/share/{id}/view` | POST | 浏览计数 |

### 分类标签

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/categories` | GET | 全部分类 |
| `/api/tags` | GET | 全部标签 |
| `/api/tags/hot` | GET | 热门标签 |

---

## 🔐 安全特性

| 特性 | 说明 |
|------|------|
| XSS 防护 | 输入输出 HTML 转义 |
| SQL 注入防护 | PreparedStatement 参数化查询 |
| CSRF 防护 | Token 验证机制 |
| 接口限流 | 频率限制防止滥用 |
| 数据加密 | 敏感信息 AES 加密 |
| 审计日志 | 操作记录可追溯 |

---

## 📊 运维监控

### 健康检查

```bash
# 后端健康状态
curl http://111.231.107.210:8080/api/health

# 数据库连接状态
curl http://111.231.107.210:8080/api/health/db
```

### 日志管理

```bash
# 查看后端日志
tail -f /opt/prompt-flow-craft/backend.log

# 查看错误日志
grep ERROR /opt/prompt-flow-craft/backend.log
```

---

## 🌐 在线体验

| 服务 | 地址 |
|------|------|
| 前端应用 | http://111.231.107.210:3000 |
| 后端 API | http://111.231.107.210:8080/api |
| API 文档 | http://111.231.107.210:8080/swagger-ui.html |

---

## 📄 开源协议

MIT License - 详见 [LICENSE](LICENSE)

---

## 👨‍💻 作者

Built with ❤️ by [KeatonLi](https://github.com/KeatonLi)

[![Star History Chart](https://api.star-history.com/svg?repos=KeatonLi/prompt-flow-craft&type=Timeline)](https://star-history.com/#KeatonLi/prompt-flow-craft&Timeline)

---

<p align="center">
  <strong>Prompt Flow Craft</strong> - 让 AI 提示词工程化繁为简
</p>
