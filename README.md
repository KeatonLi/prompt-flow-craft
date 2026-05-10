# Prompt Flow Craft

企业级 AI 提示词工程平台 | 面向 Agent + Skill 的提示词生成与管理解决方案

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.0+-4FC08D.svg)](https://vuejs.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## 核心概念

### 🤖 Agent - AI 智能体

Agent 是具有特定角色和能力的 AI 智能体。您可以定义：

- **角色定位**：Agent 的核心身份与职责
- **核心能力**：Agent 可以完成的任务
- **行为规范**：Agent 应遵循的准则
- **对话风格**：专业、友好、简洁、详细等

### ⚡ Skill - 可调用技能

Skill 是 Agent 可以调用的工具/技能，支持多种类型：

| 类型 | 说明 | 示例 |
|------|------|------|
| API | 外部 HTTP 接口 | 天气查询、翻译服务 |
| Function | 函数代码 | 数据处理、格式转换 |
| Webhook | 回调接口 | 事件通知、消息推送 |
| Data | 数据源 | 数据库查询、文件读取 |

### 🔗 Agent + Skill 关系

```
┌─────────────────────────────────────────┐
│                 Agent                    │
│  角色定位 + 核心能力 + 行为规范 + 对话风格  │
├─────────────────────────────────────────┤
│            可调用 Skills                  │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐    │
│  │ Skill 1 │ │ Skill 2 │ │ Skill 3 │    │
│  │ (API)   │ │(Function│ │(Webhook)│    │
│  └─────────┘ └─────────┘ └─────────┘    │
└─────────────────────────────────────────┘
```

Agent 可以调用一个或多个 Skill 来扩展其能力。例如：
- 一个「写作助手」Agent 可以配备 `search_reference`（查资料）、`grammar_check`（语法检查）等 Skill
- 一个「代码审查员」Agent 可以配备 `code_analysis`、`vulnerability_scan` 等 Skill

---

## 核心功能

### 🎯 Agent 提示词生成

为 AI Agent 生成专业的系统提示词：

- 角色建模与目标定义
- 能力边界与行为约束
- 多维度对话风格配置
- 流式输出，实时预览

### ⚡ Skill 提示词生成

为 Agent 构建可组合的工具技能：

- 支持 API、函数、Webhook、数据源四种类型
- 完整的参数定义与输出描述
- 认证配置、请求头、限流策略
- 自动生成符合规范的 Tool Definition

### 🌐 社区共享与发布

每个人都可以发布自己的 Agent 和 Skill：

- 一键发布到社区广场
- 查看、搜索他人分享的提示词
- 点赞、收藏、统计热度
- 开源协作，共同完善提示词生态

### 💾 智能缓存

- 相同请求参数自动缓存，降低 API 调用成本
- 哈希算法精确匹配
- 缓存命中率实时监控

### 🏷️ 智能分类

- 8 大预设分类：办公效率、内容创作、代码开发、教育培训、视频媒体、数据分析、设计创意、智能对话
- AI 自动分类与标签推荐

---

## 快速开始

### 环境要求

- Node.js 18+
- Java 17+
- MySQL 8.0+
- Maven 3.8+

### 安装部署

```bash
# 1. 克隆项目
git clone https://github.com/KeatonLi/prompt-flow-craft.git
cd prompt-flow-craft

# 2. 配置数据库
# 修改 src/main/resources/application.yml 中的数据库配置

# 3. 构建后端
mvn clean package -DskipTests

# 4. 启动后端
java -jar target/prompt-flow-craft-1.0.0.jar

# 5. 构建前端
cd frontend
npm install
npm run build

# 6. 部署
# 将 dist 目录部署到 Nginx 或其他静态服务器
```

### 环境变量

**后端：**
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` - 数据库配置
- `MODEL_API_KEY` - LLM API 密钥
- `MODEL_API_BASE_URL` - LLM API 地址（默认：https://dashscope.aliyuncs.com/compatible-mode/v1）
- `MODEL_NAME` - 模型名称（默认：qwen-turbo-2025-07-15）
- `SERVER_PORT` - 服务端口（默认：8080）

---

## 技术架构

### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17+ | 开发语言 |
| Spring Boot | 3.2.0 | 框架 |
| Hibernate | 6.4 | ORM |
| MySQL | 8.0+ | 数据库 |

### 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.4+ | 框架 |
| TypeScript | 5.0+ | 类型安全 |
| Vite | 5.0+ | 构建工具 |
| Pinia | 2.1+ | 状态管理 |

### 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                      Frontend (Vue 3)                       │
├─────────────────────────────────────────────────────────────┤
│  Home │ Generate (Agent/Skill) │ Templates │ Statistics    │
└────────────────────────┬────────────────────────────────────┘
                         │ REST API
┌────────────────────────▼────────────────────────────────────┐
│                   Backend (Spring Boot)                     │
├──────────────┬──────────────┬──────────────┬─────────────┤
│ Agent Gen    │ Skill Gen    │ History     │ Statistics  │
│ Service      │ Service      │ Service     │ Service     │
├──────────────┴──────────────┴──────────────┴─────────────┤
│                Community Service (发布/点赞/收藏)            │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│                    Data Layer (MySQL)                      │
│     AgentPrompt │ SkillPrompt │ Category │ Tag            │
└─────────────────────────────────────────────────────────────┘
```

---

## 页面导航

| 页面 | 路径 | 说明 |
|------|------|------|
| 首页 | `/` | 产品介绍与导航 |
| 生成 | `/generate` | 创建 Agent 或 Skill 提示词 |
| 广场 | `/templates` | 浏览社区分享的提示词 |
| 统计 | `/statistics` | 数据统计大盘 |

---

## API 接口

### 提示词生成

| 接口 | 方法 | 说明 |
|------|------|------|
| `POST /api/prompt/generate/agent` | POST | 生成 Agent 提示词 |
| `POST /api/prompt/generate/skill` | POST | 生成 Skill 提示词 |
| `POST /api/prompt/generate/agent/stream` | POST | 生成 Agent（流式） |
| `POST /api/prompt/generate/skill/stream` | POST | 生成 Skill（流式） |

### 社区功能

| 接口 | 方法 | 说明 |
|------|------|------|
| `GET /api/history` | GET | 获取历史记录 |
| `POST /api/history/{id}/like` | POST | 点赞 |
| `POST /api/publish` | POST | 发布到社区 |

### 分类管理

| 接口 | 方法 | 说明 |
|------|------|------|
| `GET /api/categories` | GET | 获取所有分类 |
| `GET /api/tags` | GET | 获取所有标签 |
| `GET /api/tags/hot` | GET | 获取热门标签 |

---

## 数据模型

### AgentPrompt

```json
{
  "name": "写作助手",
  "roleDescription": "你是一个专业的技术文档写作助手...",
  "capabilities": "- 撰写技术文档\n- 解释技术概念\n- 提供代码示例",
  "behaviors": "- 始终保持专业态度\n- 回答简洁明了",
  "communicationStyle": "professional",
  "generatedPrompt": "完整的系统提示词内容...",
  "authorNickname": "用户名",
  "likeCount": 42,
  "viewCount": 1024
}
```

### SkillPrompt

```json
{
  "name": "get_weather",
  "description": "获取指定城市的当前天气信息",
  "skillType": "api",
  "method": "GET",
  "endpoint": "https://api.weather.example.com/v1/current",
  "parameters": "{\"city\": {\"type\": \"string\", \"description\": \"城市名称\"}}",
  "outputDescription": "返回温度、湿度、风速等信息",
  "generatedPrompt": "完整的 Tool Definition...",
  "authorNickname": "用户名",
  "likeCount": 18,
  "viewCount": 512
}
```

---

## 开源协议

MIT License - 查看 [LICENSE](LICENSE) 了解详情

---

## 相关链接

- [项目演示](http://111.231.107.210:3000)
- [后端 API](http://111.231.107.210:8080)
- [问题反馈](https://github.com/KeatonLi/prompt-flow-craft/issues)

---

<p align="center">
  Built with ❤️ by <a href="https://github.com/KeatonLi">KeatonLi</a>
</p>
