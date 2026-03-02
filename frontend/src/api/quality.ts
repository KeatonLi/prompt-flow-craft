import request from './request';

interface QualityCheckItem {
  name: string
  passed: boolean
  message: string
  weight: number
}

interface QualityResult {
  score: number
  level: string
  items: QualityCheckItem[]
  suggestions: string[]
  summary: string
}

interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export const qualityApi = {
  async check(prompt: string): Promise<ApiResponse<QualityResult>> {
    return request.post<ApiResponse<QualityResult>>('/quality/check', {
      prompt
    })
  }
}
