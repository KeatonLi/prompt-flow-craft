import request from './request';
import type { ApiResponse, PromptRequest } from '@/types';

export const promptApi = {
  // 生成提示词
  async generate(data: PromptRequest): Promise<string> {
    try {
      console.log('API 请求数据:', data);
      const response = await request.post<ApiResponse<string>>('/generate-prompt', data);
      console.log('API 响应:', response);
      
      // 检查响应结构
      if (!response.data) {
        throw new Error('响应数据为空');
      }
      
      if (!response.data.success) {
        throw new Error(response.data.message || '生成失败');
      }
      
      if (!response.data.data) {
        throw new Error('生成结果为空');
      }
      
      return response.data.data;
    } catch (error: any) {
      console.error('API 调用失败:', error);
      console.error('错误详情:', error.response?.data || error.message);
      throw error;
    }
  },

  // 健康检查
  health(): Promise<{ status: string }> {
    return request.get<ApiResponse<{ status: string }>>('/health')
      .then(res => res.data.data);
  }
};
