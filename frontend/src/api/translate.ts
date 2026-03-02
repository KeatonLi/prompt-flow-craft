import request from './request';
import type { ApiResponse } from '@/types';

interface TranslateResult {
  original: string
  translated: string
  sourceLang: string
  targetLang: string
}

export const translateApi = {
  // 翻译提示词
  async translate(prompt: string, targetLang: string = 'en', sourceLang: string = 'auto'): Promise<ApiResponse<TranslateResult>> {
    return request.post<ApiResponse<TranslateResult>>('/prompt/translate', { 
      prompt, 
      targetLang, 
      sourceLang 
    })
  }
}
