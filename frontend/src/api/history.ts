import request from './request';
import type { ApiResponse, PromptRecord, PagedResult, HistoryQueryRequest } from '@/types';

export const historyApi = {
  // 分页查询历史记录
  getPage(params: HistoryQueryRequest): Promise<PagedResult<PromptRecord>> {
    return request.post<ApiResponse<any>>('/history/page', params)
      .then(res => {
        const result = res.data;
        return {
          list: result.data,
          total: result.total,
          totalPages: result.totalPages,
          page: result.page,
          size: result.size
        };
      });
  },

  // 获取所有历史记录
  getAll(): Promise<PromptRecord[]> {
    return request.get<ApiResponse<PromptRecord[]>>('/history/all')
      .then(res => res.data.data);
  },

  // 获取最近历史记录
  getRecent(limit: number = 20): Promise<PromptRecord[]> {
    return request.get<ApiResponse<PromptRecord[]>>('/history/recent', {
      params: { limit }
    }).then(res => res.data.data);
  },

  // 根据分类查询
  getByCategory(categoryId: number): Promise<PromptRecord[]> {
    return request.get<ApiResponse<PromptRecord[]>>(`/history/category/${categoryId}`)
      .then(res => res.data.data);
  },

  // 获取点赞数最高的提示词
  getTopLiked(page: number = 1, size: number = 20): Promise<PagedResult<PromptRecord>> {
    return request.get<ApiResponse<PagedResult<PromptRecord>>>('/history/top-liked', {
      params: { page, size }
    }).then(res => res.data.data);
  },

  // 搜索历史记录
  search(keyword: string): Promise<PromptRecord[]> {
    return request.get<ApiResponse<PromptRecord[]>>('/history/search', {
      params: { keyword }
    }).then(res => res.data.data);
  },

  // 点赞提示词
  like(id: number): Promise<void> {
    return request.post<ApiResponse<void>>(`/history/${id}/like`)
      .then(() => undefined);
  },

  // 取消点赞提示词
  unlike(id: number): Promise<void> {
    return request.post<ApiResponse<void>>(`/history/${id}/unlike`)
      .then(() => undefined);
  },

  // 更新分类
  updateCategory(id: number, categoryId: number): Promise<void> {
    return request.put<ApiResponse<void>>(`/history/${id}/category`, null, {
      params: { categoryId }
    }).then(() => undefined);
  },

  // 删除历史记录
  delete(id: number): Promise<void> {
    return request.delete<ApiResponse<void>>(`/history/${id}`)
      .then(() => undefined);
  },

  // 批量删除
  batchDelete(ids: number[]): Promise<{ deletedCount: number }> {
    return request.post<ApiResponse<{ deletedCount: number }>>('/history/batch-delete', ids)
      .then(res => res.data.data);
  },

  // 手动触发分类
  classify(id: number): Promise<ClassificationResult> {
    return request.post<ApiResponse<ClassificationResult>>(`/history/${id}/classify`)
      .then(res => res.data.data);
  },

  // 批量分类
  batchClassify(batchSize: number = 10): Promise<void> {
    return request.post<ApiResponse<void>>('/history/batch-classify', null, {
      params: { batchSize }
    }).then(() => undefined);
  }
};

import type { ClassificationResult } from '@/types';
