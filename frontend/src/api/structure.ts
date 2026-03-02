import request from './request';

interface StructureElement {
  name: string
  description: string
  present: boolean
  content: string
  weight: number
}

interface StructureResult {
  score: number
  level: string
  elements: StructureElement[]
  suggestions: string[]
  summary: string
}

interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export const structureApi = {
  async analyze(prompt: string): Promise<ApiResponse<StructureResult>> {
    return request.post<ApiResponse<StructureResult>>('/structure/analyze', {
      prompt
    })
  }
}
