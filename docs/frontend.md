# 前端文档

## 技术栈

- **Vue 3.4+** + Composition API
- **TypeScript 5.0+**
- **Vite 5.0+** 构建工具
- **Pinia** 状态管理
- **Element Plus** + **Tailwind CSS**
- **Axios** HTTP 客户端
- **markdown-it** Markdown 渲染

## 项目结构

```
frontend/src/
├── api/                # API 客户端模块
│   ├── request.ts       # Axios 实例配置
│   ├── prompt.ts        # 提示词生成 API
│   ├── history.ts       # 历史记录 API
│   ├── category.ts      # 分类 API
│   ├── tag.ts           # 标签 API
│   └── share.ts         # 提示词分享 API
├── components/          # 可复用组件
│   ├── layout/
│   │   └── AppLayout.vue        # 主布局（导航栏、侧边栏）
│   ├── history/
│   │   ├── HistoryPanel.vue    # 历史记录面板
│   │   ├── HistorySidebar.vue  # 历史记录侧边栏
│   │   └── HistoryCard.vue     # 历史记录卡片
│   ├── share/
│   │   ├── SharedPromptCard.vue      # 分享提示词卡片
│   │   ├── PublishPromptModal.vue    # 发布提示词弹窗
│   │   ├── ContactAuthorModal.vue    # 联系作者弹窗
│   │   ├── HistorySelectorModal.vue  # 历史记录选择弹窗
│   │   └── SharedPromptDetailModal.vue # 分享详情弹窗
│   ├── editor/
│   │   └── PromptEditor.vue   # 提示词编辑器
│   ├── PromptDetailModal.vue   # 详情弹窗
│   └── PromptExporter.vue      # 导出功能
├── views/               # 页面组件
│   ├── Home.vue              # 首页（产品介绍）
│   ├── Generate.vue          # 提示词生成页面（含通用/Agent/Skill 三种模式）
│   ├── Share.vue             # 提示词分享页面（开放社区，无需登录）
│   ├── Templates.vue         # 模板库页面
│   └── Statistics.vue        # 统计页面
├── stores/              # Pinia 状态管理
│   ├── index.ts             # 导出所有 store
│   ├── category.ts           # 分类状态
│   ├── tag.ts               # 标签状态
│   └── history.ts           # 历史记录状态
├── router/
│   └── index.ts             # Vue Router 配置
├── types/
│   └── index.ts             # TypeScript 类型定义
└── App.vue                 # 根组件
```

## 页面路由

| 路径 | 视图 | 说明 |
|------|------|------|
| `/` | Home | 产品介绍首页 |
| `/generate` | Generate | 创建新提示词（含通用/Agent/Skill 模式） |
| `/share` | Share | 提示词分享社区（无需登录） |
| `/templates` | Templates | 历史记录与模板浏览 |
| `/statistics` | Statistics | 数据统计大盘 |

## API 客户端

### Axios 配置 (`request.ts`)

- 基础 URL: `VITE_API_BASE_URL` 或 `/api`
- 超时时间: 30s
- 请求/响应拦截器

### API 模块

#### prompt.ts

```typescript
// 同步生成
promptApi.generate(data: PromptRequest): Promise<string>

// 流式生成（SSE）
promptApi.generateStream(
  data: PromptRequest,
  onMessage: (content: string) => void,
  onDone: (fullContent: string) => void,
  onError: (error: string) => void
): () => void  // 返回取消函数
```

#### history.ts

```typescript
historyApi.getById(id: number): Promise<PromptRecord>
historyApi.getPage(params: HistoryQueryRequest): Promise<PagedResult<PromptRecord>>
historyApi.getAll(): Promise<PromptRecord[]>
historyApi.getRecent(limit?: number): Promise<PromptRecord[]>
historyApi.getByCategory(categoryId: number): Promise<PromptRecord[]>
historyApi.search(keyword: string): Promise<PromptRecord[]>
historyApi.like(id: number): Promise<void>
historyApi.unlike(id: number): Promise<void>
historyApi.delete(id: number): Promise<void>
historyApi.batchDelete(ids: number[]): Promise<{ deletedCount: number }>
```

#### category.ts

```typescript
categoryApi.getAll(): Promise<Category[]>
categoryApi.getSystem(): Promise<Category[]>
categoryApi.getById(id: number): Promise<Category>
categoryApi.create(data: Partial<Category>): Promise<Category>
categoryApi.update(id: number, data: Partial<Category>): Promise<Category>
categoryApi.delete(id: number): Promise<void>
categoryApi.getStats(): Promise<{ categoryCounts: Record<number, number>; totalCount: number }>
```

#### tag.ts

```typescript
tagApi.getAll(): Promise<Tag[]>
tagApi.getHot(): Promise<Tag[]>
tagApi.search(keyword: string): Promise<Tag[]>
tagApi.getByPromptId(promptId: number): Promise<Tag[]>
tagApi.create(name: string, color?: string): Promise<Tag>
tagApi.batchCreate(names: string[]): Promise<Tag[]>
tagApi.delete(id: number): Promise<void>
```

#### share.ts（提示词分享）

```typescript
shareApi.getPage(params: ShareQueryRequest): Promise<PagedResult<SharedPrompt>>
shareApi.getById(id: number): Promise<SharedPrompt>
shareApi.publish(data: ShareRequest): Promise<PublishResponse>
shareApi.like(id: number): Promise<void>
shareApi.unlike(id: number): Promise<void>
shareApi.delete(id: number, token: string): Promise<void>
shareApi.getRecent(limit?: number): Promise<SharedPrompt[]>
shareApi.getTopLiked(limit?: number): Promise<SharedPrompt[]>
```

## 类型定义

### PromptRequest（提示词请求）

```typescript
interface PromptRequest {
  taskDescription: string;   // 任务描述（必填）
  targetAudience?: string;   // 目标受众
  outputFormat?: string;     // 输出格式
  constraints?: string;      // 约束条件
  examples?: string;         // 示例
  tone?: string;             // 语调风格
  length?: string;           // 内容长度
}
```

### PromptRecord（提示词记录）

```typescript
interface PromptRecord {
  id: number;
  taskDescription: string;
  targetAudience: string;
  outputFormat: string;
  constraints: string;
  examples: string;
  tone: string;
  length: string;
  generatedPrompt: string;
  requestHash: string;
  createdAt: string;
  updatedAt: string;
  hitCount: number;
  categoryId?: number;
  category?: Category;
  likeCount: number;
  isAutoTagged: boolean;
  aiTags: string[];
  tags: Tag[];
  usageScenario?: string;
  effectivenessScore?: number;
}
```

### Category（分类）

```typescript
interface Category {
  id: number;
  name: string;
  description: string;
  icon: string;
  color: string;
  sortOrder: number;
  isSystem: boolean;
  createdAt: string;
  updatedAt: string;
}
```

### Tag（标签）

```typescript
interface Tag {
  id: number;
  name: string;
  color: string;
  usageCount: number;
  isSystem: boolean;
  createdAt: string;
}
```

### SharedPrompt（分享提示词）

```typescript
interface SharedPrompt {
  id: number;
  description: string;      // 提示词描述
  promptContent: string;    // 提示词内容
  authorNickname: string;  // 作者昵称
  authorContact: string;    // 联系方式
  likeCount: number;        // 点赞数
  viewCount: number;        // 浏览数
  sourcePromptId?: number;  // 来源历史记录ID
  createdAt: string;
  updatedAt: string;
}
```

### ShareRequest（发布请求）

```typescript
interface ShareRequest {
  description: string;
  promptContent: string;
  authorNickname: string;
  authorContact: string;
  sourcePromptId?: number;
}
```

### PublishResponse（发布响应）

```typescript
interface PublishResponse {
  id: number;
  deleteToken: string;  // 删除令牌
}
```

### ApiResponse（通用响应）

```typescript
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  code: number;
}
```

### PagedResult（分页结果）

```typescript
interface PagedResult<T> {
  list: T[];
  total: number;
  totalPages: number;
  page: number;
  size: number;
}
```

### HistoryQueryRequest（历史记录查询）

```typescript
interface HistoryQueryRequest {
  page?: number;
  size?: number;
  categoryId?: number;
  keyword?: string;
  tagIds?: number[];
  sortBy?: 'createdAt' | 'likeCount';
  sortOrder?: 'ASC' | 'DESC';
}
```

## 状态管理（Pinia Stores）

### category.ts

```typescript
// state: categories, loading
// actions: fetchCategories, fetchSystemCategories
```

### tag.ts

```typescript
// state: tags, hotTags, loading
// actions: fetchTags, fetchHotTags, searchTags
```

### history.ts

```typescript
// state: records, currentRecord, loading, pagination
// actions: fetchRecords, fetchById, like, unlike, delete, batchDelete
```

## 核心组件

### AppLayout.vue

主布局组件，包含：
- 顶部导航栏
- 左侧分类导航（可折叠）
- 右侧历史记录侧边栏（可折叠）
- 主题切换

### Generate.vue

提示词生成页面：
- 左侧：参数表单（任务描述、目标受众、输出格式、约束、示例、语调、长度）
- 右侧：实时预览（Markdown 渲染）
- 支持同步/流式生成切换

### Templates.vue

模板库页面：
- 搜索栏
- 分类筛选
- 标签筛选
- 无限滚动列表
- 历史记录卡片

### PromptDetailModal.vue

提示词详情弹窗：
- 查看生成的提示词
- 复制功能
- 导出功能（Markdown/TXT/JSON）
- 点赞、评分、分类

## 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `VITE_API_BASE_URL` | `/api` | API 基础地址 |

## 品牌资源

品牌 Logo 位于 `public/` 目录：

| 文件 | 说明 |
|------|------|
| `brand-logo.png` | 品牌标识（导航栏使用） |
| `favicon.ico` | 网站 favicon |
| `favicon.svg` | SVG favicon |
| `apple-touch-icon.png` | iOS 主屏图标 |

Logo 更新后需重新部署生效。

## 常用命令

```bash
cd frontend

# 安装依赖
npm install

# 开发模式（端口 3000，代理 /api 到 localhost:8080）
npm run dev

# 生产构建
npm run build

# ESLint 检查
npm run lint
```
