import request from './request';
import type { ApiResponse, PromptRequest } from '@/types';

export const promptApi = {
  // 生成提示词
  generate(data: PromptRequest): Promise<string> {
    return request.post<ApiResponse<string>>('/generate-prompt', data)
      .then(res => res.data.data);
  },

  // 健康检查
  health(): Promise<{ status: string }> {
    return request.get<ApiResponse<{ status: string }>>('/health')
      .then(res => res.data.data);
  }
};
