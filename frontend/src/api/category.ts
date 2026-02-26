import request from './request';
import type { ApiResponse, Category } from '@/types';

export const categoryApi = {
  // 获取所有分类
  getAll(): Promise<Category[]> {
    return request.get<ApiResponse<Category[]>>('/categories')
      .then(res => res.data.data);
  },

  // 获取系统预设分类
  getSystem(): Promise<Category[]> {
    return request.get<ApiResponse<Category[]>>('/categories/system')
      .then(res => res.data.data);
  },

  // 获取单个分类
  getById(id: number): Promise<Category> {
    return request.get<ApiResponse<Category>>(`/categories/${id}`)
      .then(res => res.data.data);
  },

  // 创建分类
  create(data: Partial<Category>): Promise<Category> {
    return request.post<ApiResponse<Category>>('/categories', data)
      .then(res => res.data.data);
  },

  // 更新分类
  update(id: number, data: Partial<Category>): Promise<Category> {
    return request.put<ApiResponse<Category>>(`/categories/${id}`, data)
      .then(res => res.data.data);
  },

  // 删除分类
  delete(id: number): Promise<void> {
    return request.delete<ApiResponse<void>>(`/categories/${id}`)
      .then(() => undefined);
  }
};
