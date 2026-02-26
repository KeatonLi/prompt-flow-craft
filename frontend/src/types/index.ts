// 分类
export interface Category {
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

// 标签
export interface Tag {
  id: number;
  name: string;
  color: string;
  usageCount: number;
  isSystem: boolean;
  createdAt: string;
}

// 提示词请求
export interface PromptRequest {
  taskDescription: string;
  targetAudience?: string;
  outputFormat?: string;
  constraints?: string;
  examples?: string;
  tone?: string;
  length?: string;
}

// 提示词记录
export interface PromptRecord {
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
  isFavorite: boolean;
  isAutoTagged: boolean;
  aiTags: string[];
  tags: Tag[];
  usageScenario?: string;
  effectivenessScore?: number;
}

// 分页结果
export interface PagedResult<T> {
  list: T[];
  total: number;
  totalPages: number;
  page: number;
  size: number;
}

// 历史记录查询请求
export interface HistoryQueryRequest {
  page?: number;
  size?: number;
  categoryId?: number;
  isFavorite?: boolean;
  keyword?: string;
  tagIds?: number[];
}

// 分类结果
export interface ClassificationResult {
  categoryId: number;
  categoryName: string;
  tags: string[];
  confidence: number;
}

// API 响应
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  code: number;
}
