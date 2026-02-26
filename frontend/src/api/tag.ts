import request from './request';
import type { ApiResponse, Tag } from '@/types';

export const tagApi = {
  // 获取所有标签
  getAll(): Promise<Tag[]> {
    return request.get<ApiResponse<Tag[]>>('/tags')
      .then(res => res.data.data);
  },

  // 获取热门标签
  getHot(): Promise<Tag[]> {
    return request.get<ApiResponse<Tag[]>>('/tags/hot')
      .then(res => res.data.data);
  },

  // 搜索标签
  search(keyword: string): Promise<Tag[]> {
    return request.get<ApiResponse<Tag[]>>('/tags/search', {
      params: { keyword }
    }).then(res => res.data.data);
  },

  // 根据提示词ID获取标签
  getByPromptId(promptId: number): Promise<Tag[]> {
    return request.get<ApiResponse<Tag[]>>(`/tags/prompt/${promptId}`)
      .then(res => res.data.data);
  },

  // 创建标签
  create(name: string, color?: string): Promise<Tag> {
    return request.post<ApiResponse<Tag>>('/tags', { name, color })
      .then(res => res.data.data);
  },

  // 批量创建标签
  batchCreate(names: string[]): Promise<Tag[]> {
    return request.post<ApiResponse<Tag[]>>('/tags/batch', names)
      .then(res => res.data.data);
  },

  // 删除标签
  delete(id: number): Promise<void> {
    return request.delete<ApiResponse<void>>(`/tags/${id}`)
      .then(() => undefined);
  }
};
