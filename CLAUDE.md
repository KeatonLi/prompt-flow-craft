# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Prompt Flow Craft is an enterprise-grade AI prompt engineering platform built with Spring Boot (Java) backend and Vue 3 frontend. It provides intelligent prompt generation, management, and optimization tools for AI applications.

## Technology Stack

### Backend
- Java 17+ with Spring Boot 3.2.0
- Hibernate/JPA 6.4 for ORM
- MySQL 8.0+ database
- Maven for build

### Frontend
- Vue 3.4+ with Composition API
- TypeScript 5.0+
- Vite 5.0+ build tool
- Pinia for state management
- Element Plus + Tailwind CSS

## Common Commands

### Backend (Java/Spring Boot)

```bash
# Build
mvn clean package -DskipTests

# Run
java -jar target/prompt-flow-craft-1.0.0.jar

# Development (requires Maven)
mvn spring-boot:run
```

### Frontend (Vue)

```bash
cd frontend

# Install dependencies
npm install

# Development server (port 3000, proxies /api to localhost:8080)
npm run dev

# Build for production
npm run build

# Lint with ESLint
npm run lint
```

### Environment Variables

**Backend:**
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` - Database config
- `MODEL_API_KEY` - LLM API key (required)
- `MODEL_API_BASE_URL` - LLM API base URL (default: https://dashscope.aliyuncs.com/compatible-mode/v1)
- `MODEL_NAME` - Model name (default: qwen-turbo-2025-07-15)
- `SERVER_PORT` - Server port (default: 8080)

## Architecture

### Backend Structure (Spring Boot)
```
backend/src/main/java/com/promptflow/
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Data access (JPA)
├── entity/         # JPA entities
├── dto/            # Data transfer objects
├── config/         # Spring configuration
└── util/           # Utilities
```

### Frontend Structure (Vue 3)
```
frontend/src/
├── api/           # Axios API modules
├── components/    # Reusable Vue components
├── views/         # Page components
├── stores/        # Pinia state stores
├── router/        # Vue Router config
└── types/         # TypeScript interfaces
```

### API Response Format
All API responses use `ApiResponse<T>` wrapper with `success`, `message`, and `data` fields.

### Key API Endpoints
- `POST /api/prompt/generate` - Generate prompt (sync)
- `POST /api/prompt/generate/stream` - Generate prompt (SSE streaming)
- `POST /api/prompt/optimize` - Optimize existing prompt
- `POST /api/prompt/analyze` - Analyze prompt quality
- `GET /api/history` - Get history
- `GET /api/categories` - Get categories
- `GET /api/tags` - Get tags
- `GET /api/statistics` - Get statistics
- `GET /api/health` - Health check

### Database Tables
- `prompt_cache` - Cached/generated prompts
- `prompt_category` - 8 preset categories
- `prompt_tag` - Tags for prompts
- `prompt_tag_relation` - Many-to-many relationship

## Routes/Pages

| Path | View | Description |
|------|------|-------------|
| `/` | Home | Product introduction |
| `/generate` | Generate | Create Agent/Skill prompts (Agent/Skill tab selector) |
| `/templates` | Templates | Prompt generation history with Agent/Skill filter tabs |
| `/statistics` | Statistics | Data dashboard |

## Key Implementation Details

### Generate Page (Agent/Skill)
- **Agent Mode**: name, roleDescription, capabilities, behaviors, communicationStyle
- **Skill Mode**: name, description, skillType (api/function/webhook/data), method, endpoint, parameters, outputDescription
- **流式输出**: SSE streaming with markdown rendering
- **布局**: 双栏网格布局 (表单左 | 结果右)，响应式移动端堆叠

### Templates Page (History)
- **Filter Tabs**: All / Agent / Skill type filter
- **数据来源**: 用户生成的提示词历史记录
- **社区**: 独立的分享提示词区域

### API Streaming
- `generateAgentStream()` / `generateSkillStream()` - SSE 流式生成
- 50ms 节流渲染防止卡顿
- 直接渲染 markdown不过滤 <think> 标签
