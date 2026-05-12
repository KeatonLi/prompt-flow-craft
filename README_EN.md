# Prompt Flow Craft

### Enterprise-Grade AI Prompt Engineering Platform

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4+-4FC08D.svg)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0+-3178C6.svg)](https://www.typescriptlang.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-4479A1.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

[**English**](README_EN.md) | [中文](README.md)

---

## 🎯 Overview

**Prompt Flow Craft** is an enterprise-grade AI prompt engineering platform, designed with a **Dual-Engine Architecture (Agent + Skill)**. It supports rapid construction and deployment of AI agents through an innovative approach that decomposes prompt engineering into two core modules: the **Agent Engine** handles role modeling and dialogue decision-making, while the **Skill Engine** manages tool invocation and capability extension. Together, they create a complete AI application ecosystem.

---

## 🏛️ System Architecture

### Six-Layer Microservices Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER                                │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │
│  │   Home      │  │  Generate   │  │  Templates   │  │ Statistics  │  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘  │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │ HTTP / SSE
┌────────────────────────────────▼────────────────────────────────────────┐
│                       APPLICATION LAYER                                   │
│  ┌──────────────────────────────────────────────────────────────────┐   │
│  │                     RESTful API Gateway                             │   │
│  └──────────────────────────────────────────────────────────────────┘   │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │ Interface
┌────────────────────────────────▼────────────────────────────────────────┐
│                        BUSINESS LAYER                                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌───────────┐  │
│  │   Agent      │  │    Skill     │  │   History    │  │ Community │  │
│  │   Service    │  │   Service    │  │   Service    │  │  Service  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  └───────────┘  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌───────────┐  │
│  │  Classify    │  │    Auth      │  │  Analytics   │  │    LLM   │  │
│  │   Service    │  │   Service    │  │   Service    │  │  Client  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  └───────────┘  │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │ Interface
┌────────────────────────────────▼────────────────────────────────────────┐
│                          DOMAIN LAYER                                    │
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
│                      INFRASTRUCTURE LAYER                                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌───────────┐  │
│  │  Repository  │  │     JPA      │  │    Cache     │  │    SSO    │  │
│  │    Layer     │  │   Hibernate  │  │   Redis     │  │  Gateway  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  └───────────┘  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌───────────┐  │
│  │  Database    │  │   Message   │  │    File     │  │   Log     │  │
│  │   MySQL 8   │  │    Queue    │  │   Storage   │  │  Monitor  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  └───────────┘  │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │
┌────────────────────────────────▼────────────────────────────────────────┐
│                       EXTERNAL SERVICES                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐              │
│  │   LLM API    │  │  DashScope  │  │   Network   │              │
│  │  (Qwen/A4)   │  │  (Alibaba)  │  │  HTTP/WS   │              │
│  └──────────────┘  └──────────────┘  └──────────────┘              │
└─────────────────────────────────────────────────────────────────────────┘
```

### Frontend Three-Tier SPA Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         VIEW LAYER                                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │
│  │    Home     │  │   Generate  │  │  Templates  │  │ Statistics  │  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │
│  │ HistoryCard │  │  ShareCard  │  │ DetailModal│  │PublishModal│  │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘  │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │ Props / Events
┌────────────────────────────────▼────────────────────────────────────────┐
│                    COMPOSABLE LAYER                                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                │
│  │  useAdmin   │  │  usePrompt  │  │ useHistory  │                │
│  └──────────────┘  └──────────────┘  └──────────────┘                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                │
│  │  useShare   │  │   useToast   │  │  useModal   │                │
│  └──────────────┘  └──────────────┘  └──────────────┘                │
└────────────────────────────────┬────────────────────────────────────────┘
                                 │ Store / API
┌────────────────────────────────▼────────────────────────────────────────┐
│                       SERVICE LAYER                                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                │
│  │  promptApi   │  │  historyApi  │  │  shareApi   │                │
│  └──────────────┘  └──────────────┘  └──────────────┘                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                │
│  │   request   │  │   useAdmin   │  │   stores    │                │
│  └──────────────┘  └──────────────┘  └──────────────┘                │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## ✨ Core Features

### 🤖 Agent Engine

Agent is an AI intelligent agent with specific role positioning and capabilities, using a **Tripartite Design** of Role Modeling + Capability Boundaries + Behavioral Specifications:

| Module | Description | Technical Implementation |
|--------|-------------|------------------------|
| Role Definition | Agent's core identity and expertise | Role Description Embedding |
| Core Capabilities | 10+ task types Agent can accomplish | Capability Vector |
| Behavioral Constraints | Agent decision boundaries | Behavior Constraint Model |
| Communication Style | Professional/Friendly/Concise/Creative | Style Profile Mapping |

**Agent Generation Flow:**
```
User Input → Role Modeling → Capability Orchestration → Constraint Injection → LLM Inference → Streaming Output → Auto-Save
```

### ⚡ Skill Engine

Skill is an external tool callable by Agent, supporting **Four Skill Types**:

| Type | Protocol | Use Cases |
|------|----------|-----------|
| API | HTTP/REST | Weather query, Translation, Payment |
| Function | JSON RPC | Data processing, Format conversion |
| Webhook | HTTP Callback | Event notification, Message push |
| Data | SQL/NoSQL | Database query, File read |

**Skill Definition Structure:**
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

### 🌐 Community Hub

A prompt sharing ecosystem built on **Social Graph**:

- **Publishing System**: One-click sharing to community square
- **Interaction System**: Like, Favorite, Comment, Rate
- **Recommendation System**: Heat-based sorting + Personalized recommendation
- **Governance System**: Admin mode, Community reporting, Deduplication

### 🧠 AI Classification

Automatic classification using **Rule Engine + LLM Hybrid Strategy**:

```
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│  Keyword Match  │  →   │  LLM Inference  │  →   │  Result Merge   │
└─────────────────┘      └─────────────────┘      └─────────────────┘
         ↓                                                   ↓
   Confidence > 0.7                               Final classification + Tags
```

**8 Preset Categories:**
Office Efficiency | Content Creation | Code Development | Education & Training | Video & Media | Data Analysis | Design & Creativity | Intelligent Dialogue

### 🏷️ Auto Tagging

Automatic 5-tag generation on save using:

- **Semantic Analysis**: Understanding core topics
- **Domain Recognition**: Matching technical/business/creative tags
- **Usage Tracking**: Tag frequency statistics

---

## 🗄️ Database Architecture

### ER Diagram

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

### Unified Storage Strategy

All prompt types (Agent/Skill/Generic) are stored in `prompt_resource` table, distinguished by `prompt_type`:

```sql
-- Unified table structure supporting multiple types
CREATE TABLE prompt_resource (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    prompt_type     VARCHAR(20) NOT NULL,      -- 'agent' | 'skill'
    name            VARCHAR(100),
    -- Agent-specific fields
    role_description TEXT,
    capabilities    TEXT,
    behaviors       TEXT,
    communication_style VARCHAR(50),
    -- Skill-specific fields
    skill_type      VARCHAR(20),
    method          VARCHAR(10),
    endpoint        TEXT,
    parameters      JSON,
    -- Common fields
    generated_prompt TEXT NOT NULL,
    prompt_summary  VARCHAR(500),
    like_count      INT DEFAULT 0,
    view_count      INT DEFAULT 0,
    -- Classification & Tags
    category_id    BIGINT,
    is_auto_tagged  BOOLEAN DEFAULT FALSE,
    ai_tags         JSON,
    -- Metadata
    created_at      DATETIME,
    updated_at      DATETIME,
    INDEX idx_prompt_type (prompt_type),
    INDEX idx_category (category_id),
    INDEX idx_created (created_at)
);
```

---

## 📦 Tech Stack

### Backend

| Layer | Technology | Version | Notes |
|-------|------------|---------|-------|
| Runtime | Java | 17+ | LTS |
| Framework | Spring Boot | 3.2.0 | Convention over Configuration |
| ORM | Hibernate/JPA | 6.4 | Object-Relational Mapping |
| Database | MySQL | 8.0+ | Master-slave support |
| Connection Pool | HikariCP | - | High performance |
| Validation | Hibernate Validator | - | Bean Validation |
| API | Spring MVC | - | RESTful style |
| Logging | SLF4J + Logback | - | Configurable |
| Build | Maven | 3.8+ | Dependency management |

### Frontend

| Layer | Technology | Version | Notes |
|-------|------------|---------|-------|
| Framework | Vue | 3.4+ | Composition API |
| Language | TypeScript | 5.0+ | Type safety |
| Build | Vite | 5.0+ | Lightning fast |
| Router | Vue Router | 4.0+ | SPA routing |
| State | Pinia | 2.1+ | Composition Store |
| UI | Element Plus | 2.4+ | Enterprise components |
| Styling | Tailwind CSS | 3.0+ | Atomic CSS |
| Charts | ECharts | 5.0+ | Data visualization |
| Rendering | Markdown-it | - | Markdown parsing |
| Highlighting | highlight.js | - | Syntax highlighting |

---

## 🚀 Quick Start

### Requirements

| Environment | Version | Notes |
|------------|---------|-------|
| Node.js | 18+ | Frontend runtime |
| Java | 17+ | Backend runtime |
| MySQL | 8.0+ | Database |
| Maven | 3.8+ | Build tool |

### Deployment

```bash
# 1. Clone the project
git clone https://github.com/KeatonLi/prompt-flow-craft.git
cd prompt-flow-craft

# 2. Initialize database
mysql -u root -p < scripts/init.sql

# 3. Configure environment variables
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=prompt_flow
export DB_USERNAME=root
export DB_PASSWORD=your_password
export MODEL_API_KEY=your_api_key

# 4. Build backend
cd backend
mvn clean package -DskipTests

# 5. Start backend
java -jar target/prompt-flow-craft-1.0.0.jar

# 6. Build frontend
cd ../frontend
npm install
npm run build

# 7. Deploy static files
# Deploy dist to Nginx
```

### Docker

```bash
# One-click start
docker-compose up -d
```

---

## 📡 API Reference

### Prompt Generation

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/prompt/generate/agent` | POST | Generate Agent prompt |
| `/api/prompt/generate/skill` | POST | Generate Skill prompt |
| `/api/prompt/generate/agent/stream` | POST | **Streaming** Agent |
| `/api/prompt/generate/skill/stream` | POST | **Streaming** Skill |

### History Management

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/history/agent` | POST | Save Agent |
| `/api/history/skill` | POST | Save Skill |
| `/api/history/page` | POST | Paginated query |
| `/api/history/{id}` | GET | Detail query |
| `/api/history/{id}/like` | POST | Like |
| `/api/history/{id}/classify` | POST | AI classification |

### Community

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/share` | GET | Share list |
| `/api/share/recent` | GET | Recent shares |
| `/api/share/publish` | POST | Publish share |
| `/api/share/{id}/like` | POST | Like |
| `/api/share/{id}/view` | POST | View count |

### Categories & Tags

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/categories` | GET | All categories |
| `/api/tags` | GET | All tags |
| `/api/tags/hot` | GET | Hot tags |

---

## 🔐 Security Features

| Feature | Description |
|---------|-------------|
| XSS Protection | Input/Output HTML escaping |
| SQL Injection Prevention | PreparedStatement parameterization |
| CSRF Protection | Token verification |
| Rate Limiting | Frequency control |
| Data Encryption | AES encryption for sensitive data |
| Audit Logging | Operation traceability |

---

## 📊 Operations & Monitoring

### Health Check

```bash
# Backend health
curl http://111.231.107.210:8080/api/health

# Database connection
curl http://111.231.107.210:8080/api/health/db
```

### Log Management

```bash
# View backend logs
tail -f /opt/prompt-flow-craft/backend.log

# View error logs
grep ERROR /opt/prompt-flow-craft/backend.log
```

---

## 🌐 Live Demo

| Service | URL |
|---------|-----|
| Frontend | http://111.231.107.210:3000 |
| Backend API | http://111.231.107.210:8080/api |
| API Docs | http://111.231.107.210:8080/swagger-ui.html |

---

## 📄 License

MIT License - See [LICENSE](LICENSE)

---

## 👨‍💻 Author

Built with ❤️ by [KeatonLi](https://github.com/KeatonLi)

[![Star History Chart](https://api.star-history.com/svg?repos=KeatonLi/prompt-flow-craft&type=Timeline)](https://star-history.com/#KeatonLi/prompt-flow-craft&Timeline)

---

<p align="center">
  <strong>Prompt Flow Craft</strong> - Simplifying Enterprise AI Prompt Engineering
</p>
