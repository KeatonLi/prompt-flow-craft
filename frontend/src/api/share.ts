import request from './request';
import type { ApiResponse, SharedPrompt, ShareRequest, PublishResponse, ShareQueryRequest } from '@/types';

export const shareApi = {
  // 发布分享
  publish(data: ShareRequest): Promise<PublishResponse> {
    return request.post<ApiResponse<PublishResponse>>('/share', data)
      .then(res => res.data.data);
  },

  // 获取分享列表
  getList(params: ShareQueryRequest): Promise<{ list: SharedPrompt[]; total: number; totalPages: number; page: number; size: number }> {
    return request.get<ApiResponse<any>>('/share', { params })
      .then(res => res.data);
  },

  // 获取分享详情
  getById(id: number): Promise<SharedPrompt> {
    return request.get<ApiResponse<SharedPrompt>>(`/share/${id}`)
      .then(res => res.data.data);
  },

  // 获取最近分享
  getRecent(limit: number = 20): Promise<SharedPrompt[]> {
    return request.get<ApiResponse<SharedPrompt[]>>('/share/recent', {
      params: { limit }
    }).then(res => res.data.data);
  },

  // 获取热门点赞
  getTopLiked(page: number = 1, size: number = 20): Promise<{ list: SharedPrompt[]; total: number; totalPages: number }> {
    return request.get<ApiResponse<any>>('/share/top-liked', { params: { page, size } })
      .then(res => res.data);
  },

  // 删除分享
  delete(id: number, token: string): Promise<void> {
    return request.delete<ApiResponse<void>>(`/share/${id}`, { params: { token } })
      .then(() => undefined);
  },

  // 点赞
  like(id: number): Promise<void> {
    return request.post<ApiResponse<void>>(`/share/${id}/like`)
      .then(() => undefined);
  },

  // 取消点赞
  unlike(id: number): Promise<void> {
    return request.post<ApiResponse<void>>(`/share/${id}/unlike`)
      .then(() => undefined);
  }
};