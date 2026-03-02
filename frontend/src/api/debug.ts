import request from './request';
import type { ApiResponse } from '@/types';

interface DebugResult {
  problemCount: number
  summary: string
  issues: Array<{
    type: string
    severity: string
    description: string
    suggestion: string
  }>
  fixedPrompt: string
}

export const debugApi = {
  // 调试提示词
  async debug(prompt: string): Promise<ApiResponse<DebugResult>> {
    return request.post<ApiResponse<DebugResult>>('/prompt/debug', { prompt })
  }
}
