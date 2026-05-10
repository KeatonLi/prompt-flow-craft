import request from './request';

export interface AgentRecord {
  id: number;
  name: string;
  roleDescription: string;
  capabilities: string;
  behaviors: string;
  communicationStyle: string;
  generatedPrompt: string;
  authorId?: number;
  authorNickname?: string;
  likeCount: number;
  viewCount: number;
  createdAt: string;
  updatedAt: string;
  // For compatibility
  taskDescription: string;
  promptSummary: string;
}

export interface SkillRecord {
  id: number;
  name: string;
  description: string;
  skillType: 'api' | 'function' | 'webhook' | 'data';
  method?: string;
  endpoint?: string;
  parameters?: string;
  outputDescription?: string;
  generatedPrompt: string;
  authorId?: number;
  authorNickname?: string;
  likeCount: number;
  viewCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface PagedResult<T> {
  list: T[];
  total: number;
  totalPages: number;
  page: number;
  size: number;
}

export const promptRecordApi = {
  // Agent records
  getAllAgents(): Promise<AgentRecord[]> {
    return request.get<any>('/records/agents')
      .then(res => res.data.data);
  },

  getRecentAgents(limit: number = 20): Promise<AgentRecord[]> {
    return request.get<any>('/records/agents/recent', {
      params: { limit }
    }).then(res => res.data.data);
  },

  getAgentById(id: number): Promise<AgentRecord> {
    return request.get<any>(`/records/agents/${id}`)
      .then(res => res.data.data);
  },

  getAgentPage(params: {
    page?: number;
    size?: number;
    sortBy?: 'createdAt' | 'likeCount';
  }): Promise<PagedResult<AgentRecord>> {
    return request.get<any>('/records/agents/page', { params })
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

  deleteAgent(id: number): Promise<void> {
    return request.delete<any>(`/records/agents/${id}`)
      .then(() => undefined);
  },

  // Skill records
  getAllSkills(): Promise<SkillRecord[]> {
    return request.get<any>('/records/skills')
      .then(res => res.data.data);
  },

  getRecentSkills(limit: number = 20): Promise<SkillRecord[]> {
    return request.get<any>('/records/skills/recent', {
      params: { limit }
    }).then(res => res.data.data);
  },

  getSkillById(id: number): Promise<SkillRecord> {
    return request.get<any>(`/records/skills/${id}`)
      .then(res => res.data.data);
  },

  getSkillPage(params: {
    page?: number;
    size?: number;
    sortBy?: 'createdAt' | 'likeCount';
  }): Promise<PagedResult<SkillRecord>> {
    return request.get<any>('/records/skills/page', { params })
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

  deleteSkill(id: number): Promise<void> {
    return request.delete<any>(`/records/skills/${id}`)
      .then(() => undefined);
  }
};