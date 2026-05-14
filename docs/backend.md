# 后端文档

## 技术栈

- **Java 17+** / Spring Boot 3.2.0
- **Hibernate/JPA 6.4** ORM
- **MySQL 8.0+** 数据库
- **Maven** 构建工具

## 项目结构

```
backend/
├── src/main/java/com/promptflow/
├── controller/           # REST 控制器
│   ├── PromptController.java       # 提示词生成（同步/流式）
│   ├── HistoryController.java      # 历史记录 CRUD
│   ├── CategoryController.java     # 分类管理
│   ├── TagController.java          # 标签管理
│   ├── StatisticsController.java   # 统计接口
│   ├── SharedPromptController.java # 提示词分享（开放社区）
│   └── WebController.java          # Web 页面路由
├── service/             # 业务逻辑
│   ├── PromptService.java          # 核心生成/优化逻辑
│   ├── PromptHistoryService.java   # 历史记录服务
│   ├── PromptCacheService.java     # 哈希缓存
│   ├── PromptClassificationService.java  # AI 自动分类
│   ├── StatisticsService.java      # 统计数据
│   ├── SharedPromptService.java    # 提示词分享服务
│   └── quality/                    # 质量分析模块
├── strategy/prompt/     # 策略模式实现
│   ├── PromptStrategy.java         # 策略接口
│   ├── PromptStrategyFactory.java  # 工厂类
│   ├── StrategyContext.java        # 上下文
│   └── impl/                       # 具体策略实现
│       ├── GeneratePromptStrategy.java      # 四层架构生成
│       ├── GeneralOptimizationStrategy.java  # 综合优化
│       ├── ClarityOptimizationStrategy.java # 清晰度优化
│       ├── SpecificityOptimizationStrategy.java # 具体性优化
│       ├── StructureOptimizationStrategy.java  # 结构优化
│       └── ExamplesOptimizationStrategy.java  # 示例优化
├── repository/          # JPA 数据访问
│   ├── PromptCacheRepository.java    # 提示词缓存
│   ├── PromptCategoryRepository.java # 分类
│   ├── PromptTagRepository.java      # 标签
│   └── SharedPromptRepository.java   # 分享提示词
├── entity/              # 数据库实体
│   ├── PromptCache.java           # 主实体（30+字段）
│   ├── PromptCategory.java         # 分类实体
│   ├── PromptTag.java              # 标签实体
│   └── SharedPrompt.java           # 分享提示词实体
├── dto/                  # 数据传输对象
│   ├── PromptRequest.java          # 生成请求
│   ├── OptimizeRequest.java        # 优化请求
│   ├── HistoryResponse.java        # 历史记录响应
│   ├── ShareRequest.java           # 发布分享请求
│   ├── SharedPromptResponse.java   # 分享响应
│   ├── PublishResponse.java        # 发布结果响应
│   └── llm/                        # LLM 相关 DTO
├── client/llm/          # LLM 客户端
│   ├── LLMClient.java              # 接口
│   └── OpenAICompatibleClient.java # OpenAI 兼容实现
├── config/              # Spring 配置
│   ├── CorsConfig.java             # 跨域配置
│   ├── DataInitializer.java        # 数据初始化（8 分类）
│   └── GlobalExceptionHandler.java # 全局异常处理
└── util/                # 工具类
└── src/main/resources/
    └── prompts/          # 提示词模板（与代码分离）
        ├── agent_prompt_template.txt      # Agent 提示词模板
        ├── skill_prompt_template.txt     # Skill 提示词模板
        ├── generate_prompt_template.txt  # 通用生成模板
        ├── classification_prompt_template.txt  # 分类模板
        └── five_tags_prompt_template.txt # 打标签模板
```

## 提示词模板

提示词模板独立存放于 `resources/prompts/` 目录，便于迭代优化，无需修改代码。

### 模板变量说明

| 模板 | 变量 | 说明 |
|------|------|------|
| agent_prompt_template | `{name}`, `{roleDescription}`, `{capabilities}`, `{behaviors}`, `{communicationStyle}` | Agent 生成 |
| skill_prompt_template | `{name}`, `{description}`, `{skillType}`, `{method}`, `{endpoint}`, `{parameters}`, `{outputDescription}` | Skill 生成 |
| generate_prompt_template | `{taskDescription}`, `{targetAudience}`, `{outputFormat}`, `{tone}`, `{length}`, `{constraints}`, `{examples}` | 通用生成 |
| classification_prompt_template | `{categoryList}`, `{taskDescription}`, `{targetAudience}`, `{outputFormat}`, `{tone}`, `{generatedPrompt}` | 自动分类 |
| five_tags_prompt_template | `{name}`, `{roleDescription}`, `{capabilities}`, `{behaviors}`, `{description}`, `{skillType}`, `{method}`, `{generatedPrompt}` | AI 打标签 |

## 数据库模型

### prompt_cache（主表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| task_description | VARCHAR | 任务描述 |
| target_audience | VARCHAR | 目标受众 |
| output_format | VARCHAR | 输出格式 |
| constraints | VARCHAR | 约束条件 |
| examples | TEXT | 示例 |
| tone | VARCHAR | 语调风格 |
| length | VARCHAR | 内容长度 |
| generated_prompt | TEXT | 生成的提示词 |
| prompt_summary | VARCHAR | 提示词摘要 |
| request_hash | VARCHAR | 请求哈希（唯一索引） |
| hit_count | INT | 缓存命中次数 |
| like_count | INT | 点赞数 |
| user_rating | DOUBLE | 用户评分 |
| category_id | BIGINT | 分类 ID |
| is_auto_tagged | BOOLEAN | 是否 AI 自动标记 |
| ai_tags | JSON | AI 标签 |
| input_tokens | INT | 输入 token 数 |
| output_tokens | INT | 输出 token 数 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### prompt_category（8 个预设分类）

| id | name | description | icon | color |
|----|------|-------------|------|-------|
| 1 | 办公效率 | ... | 💼 | #xxx |
| 2 | 内容创作 | ... | ✍️ | #xxx |
| 3 | 代码开发 | ... | 💻 | #xxx |
| 4 | 教育培训 | ... | 📚 | #xxx |
| 5 | 视频媒体 | ... | 🎬 | #xxx |
| 6 | 数据分析 | ... | 📊 | #xxx |
| 7 | 设计创意 | ... | 🎨 | #xxx |
| 8 | 智能对话 | ... | 💬 | #xxx |

## API 接口

### 提示词生成

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/prompt/generate` | 同步生成提示词 |
| POST | `/api/prompt/generate/stream` | SSE 流式生成 |
| POST | `/api/prompt/optimize` | 优化已有提示词 |
| POST | `/api/prompt/analyze` | 质量分析 |

### 历史记录

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/history/page` | 分页查询 |
| GET | `/api/history/{id}` | 获取详情 |
| POST | `/api/history/{id}/like` | 点赞（60s 冷却） |
| POST | `/api/history/{id}/unlike` | 取消点赞 |
| POST | `/api/history/{id}/rate` | 评分（1-5 星） |
| POST | `/api/history/{id}/classify` | 手动分类 |
| POST | `/api/history/batch-delete` | 批量删除 |

### 分类管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/categories` | 获取所有分类 |
| GET | `/api/categories/system` | 获取系统预设分类 |
| GET | `/api/categories/stats` | 分类统计 |

### 标签管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/tags` | 获取所有标签 |
| GET | `/api/tags/hot` | 热门标签 |
| GET | `/api/tags/search` | 搜索标签 |
| POST | `/api/tags` | 创建标签 |
| POST | `/api/tags/batch` | 批量创建 |
| DELETE | `/api/tags/{id}` | 删除标签 |

### 提示词分享（开放社区，无需登录）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/share` | 发布提示词 |
| GET | `/api/share` | 分页列表 |
| GET | `/api/share/{id}` | 详情（含浏览量+1） |
| DELETE | `/api/share/{id}?token=xxx` | 删除（需令牌） |
| POST | `/api/share/{id}/like` | 点赞 |
| POST | `/api/share/{id}/unlike` | 取消点赞 |
| GET | `/api/share/recent` | 最新分享 |
| GET | `/api/share/top-liked` | 热门点赞 |

### 其他

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/statistics` | 使用统计 |
| GET | `/api/health` | 健康检查 |

## 核心架构

### 四层架构模型

提示词生成采用四层架构：

1. **核心定义层** - 角色建模、目标定义
2. **交互接口层** - 输入规范、输出格式
3. **内部处理层** - 工作流程、决策逻辑
4. **全局约束层** - 安全边界、性能优化

### 策略模式

优化策略通过策略模式实现，支持多种优化方向：

- `GeneralOptimizationStrategy` - 综合优化
- `ClarityOptimizationStrategy` - 清晰度优化
- `SpecificityOptimizationStrategy` - 具体性优化
- `StructureOptimizationStrategy` - 结构优化
- `ExamplesOptimizationStrategy` - 示例优化

### 缓存机制

基于 SHA-256 请求哈希的缓存机制，相同参数生成相同结果。

### 流式输出

支持 SSE (Server-Sent Events) 流式输出，实时展示生成进度。

## 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `DB_HOST` | localhost | 数据库地址 |
| `DB_PORT` | 3306 | 数据库端口 |
| `DB_NAME` | prompt_flow_craft | 数据库名 |
| `DB_USERNAME` | root | 用户名 |
| `DB_PASSWORD` | - | 密码 |
| `MODEL_API_KEY` | - | LLM API Key（必填） |
| `MODEL_API_BASE_URL` | https://dashscope.aliyuncs.com/compatible-mode/v1 | API 地址 |
| `MODEL_NAME` | qwen-turbo-2025-07-15 | 模型名称 |
| `SERVER_PORT` | 8080 | 服务端口 |

## 常用命令

```bash
# 构建
mvn clean package -DskipTests

# 运行
java -jar target/prompt-flow-craft-1.0.0.jar

# 开发模式
mvn spring-boot:run
```
