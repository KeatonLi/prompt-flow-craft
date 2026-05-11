import request from './request';
import type { ApiResponse, PromptRequest } from '@/types';
import type { AgentRecord, SkillRecord } from './promptRecord';

// 清理内容中的推理标记 (<think>...</think>)
export interface AgentPromptRequest {
  name: string;
  roleDescription: string;
  capabilities?: string;
  behaviors?: string;
  communicationStyle?: string;
}

export interface SkillPromptRequest {
  name: string;
  description: string;
  skillType?: 'api' | 'function' | 'webhook' | 'data';
  method?: string;
  endpoint?: string;
  parameters?: string;
  outputDescription?: string;
}

export interface SaveAgentRequest {
  name: string;
  roleDescription: string;
  capabilities?: string;
  behaviors?: string;
  communicationStyle?: string;
  generatedPrompt: string;
}

export interface SaveSkillRequest {
  name: string;
  description: string;
  skillType?: string;
  method?: string;
  endpoint?: string;
  parameters?: string;
  outputDescription?: string;
  generatedPrompt: string;
}

export const promptApi = {
  // 同步生成提示词（非流式）
  async generate(data: PromptRequest): Promise<string> {
    try {
      console.log('API 请求数据:', data);
      const response = await request.post<ApiResponse<string>>('/generate-prompt', data);
      console.log('API 响应:', response);

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

  // 同步生成 Agent 提示词
  async generateAgent(data: AgentPromptRequest): Promise<AgentRecord> {
    const response = await request.post<ApiResponse<AgentRecord>>('/generate-agent', data);
    if (!response.data.success) {
      throw new Error(response.data.message || 'Agent生成失败');
    }
    return response.data.data;
  },

  // 同步生成 Skill 提示词
  async generateSkill(data: SkillPromptRequest): Promise<SkillRecord> {
    const response = await request.post<ApiResponse<SkillRecord>>('/generate-skill', data);
    if (!response.data.success) {
      throw new Error(response.data.message || 'Skill生成失败');
    }
    return response.data.data;
  },

  // 流式生成提示词（Server-Sent Events）
  generateStream(
    data: PromptRequest,
    onMessage: (content: string) => void,
    onDone: (fullContent: string) => void,
    onError: (error: string) => void
  ): () => void {
    const apiUrl = import.meta.env.VITE_API_BASE_URL || '/api';
    const url = `${apiUrl}/generate-prompt/stream`;

    console.log('开始流式请求:', url);

    const controller = new AbortController();
    let fullContent = '';
    let buffer = '';

    // SSE 解析状态
    let currentEventName = '';
    let currentEventData = '';

    function processEvent() {
      if (!currentEventData) return;

      if (currentEventName === 'message') {
        // 处理消息事件
        try {
          const parsed = JSON.parse(currentEventData);
          if (parsed.content) {
            fullContent += parsed.content;
            onMessage(parsed.content);
          } else if (parsed.done) {
            // done 事件不需要额外处理
          }
        } catch {
          // 非 JSON 当纯文本处理
          fullContent += currentEventData;
          onMessage(currentEventData);
        }
      } else if (currentEventName === 'error') {
        onError(currentEventData);
      }

      currentEventData = '';
      currentEventName = '';
    }

    function processSSELine(line: string) {
      const trimmed = line.trim();

      if (trimmed.startsWith('event:')) {
        // 事件名行
        currentEventName = trimmed.substring(6).trim();
      } else if (trimmed.startsWith('data:')) {
        // 数据行 - 追加到当前数据
        // 注意：空 data: 行（如 "data:"）代表一个空行，需要保留
        const dataContent = trimmed.substring(5); // 不 trim，这样空行也能被识别
        if (currentEventData && dataContent) {
          // 如果当前有数据且新数据非空，加换行符
          currentEventData += '\n';
        } else if (currentEventData && !dataContent) {
          // 当前有数据但新数据为空（空行），加换行符表示空行
          currentEventData += '\n';
        }
        currentEventData += dataContent;
      } else if (trimmed === '') {
        // 空行 - 事件结束
        processEvent();
      }
      // 忽略其他行
    }

    fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'text/event-stream',
      },
      body: JSON.stringify(data),
      signal: controller.signal,
    })
      .then(async (response) => {
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        if (!response.body) {
          throw new Error('响应体为空');
        }

        const reader = response.body.getReader();
        const decoder = new TextDecoder();

        while (true) {
          const { done, value } = await reader.read();
          if (done) break;

          const chunk = decoder.decode(value, { stream: true });
          buffer += chunk;

          // 按换行分割
          const lines = buffer.split('\n');
          // 保留最后一行（可能不完整）
          buffer = lines.pop() || '';

          for (const line of lines) {
            processSSELine(line);
          }
        }

        // 处理缓冲区中剩余的数据
        if (buffer.trim()) {
          processSSELine(buffer);
        }

        console.log('流式读取完成，总长度:', fullContent.length);
        onDone(fullContent);
      })
      .catch((error) => {
        if (error.name === 'AbortError') {
          console.log('流式请求被取消');
          onDone(fullContent);
        } else {
          console.error('流式请求失败:', error);
          onError(error.message || '流式生成失败');
        }
      });

    return () => {
      console.log('取消流式请求');
      controller.abort();
    };
  },

  // 流式生成 Agent 提示词
  generateAgentStream(
    data: AgentPromptRequest,
    onMessage: (content: string) => void,
    onDone: (fullContent: string) => void,
    onError: (error: string) => void
  ): () => void {
    const apiUrl = import.meta.env.VITE_API_BASE_URL || '/api';
    const url = `${apiUrl}/generate-agent/stream`;

    console.log('开始Agent流式请求:', url);

    const controller = new AbortController();
    let fullContent = '';
    let buffer = '';

    // SSE 解析状态
    let currentEventName = '';
    let currentEventData = '';

    function processEvent() {
      if (!currentEventData) return;

      console.log('[Agent SSE] processEvent: currentEventName=', currentEventName, 'currentEventData=', currentEventData);

      if (currentEventName === 'message') {
        try {
          const parsed = JSON.parse(currentEventData);
          console.log('[Agent SSE] parsed content:', parsed.content);
          if (parsed.content) {
            fullContent += parsed.content;
            onMessage(parsed.content);
          }
        } catch (e) {
          // JSON 解析失败，使用原始文本（SSE data 内容本身可能包含换行符）
          console.error('[Agent SSE] JSON parse error:', e, 'data:', currentEventData);
          fullContent += currentEventData;
          onMessage(currentEventData);
        }
      } else if (currentEventName === 'error') {
        onError(currentEventData);
      }

      currentEventData = '';
      currentEventName = '';
    }

    function processSSELine(line: string) {
      // Debug: show raw line
      console.log('[Agent SSE] raw line:', JSON.stringify(line));

      if (line.startsWith('event:')) {
        // 如果有累积的数据，先处理之前的事件
        if (currentEventData) {
          processEvent();
        }
        currentEventName = line.substring(6).trim();
        console.log('[Agent SSE] event name:', currentEventName);
      } else if (line.startsWith('data:')) {
        // 提取 data 内容
        const dataContent = line.substring(5); // 不 trim，保留原始内容
        console.log('[Agent SSE] data content:', JSON.stringify(dataContent));

        if (dataContent === '') {
          // 空 data: 表示内容中的空行，添加换行符
          currentEventData += '\n';
        } else {
          currentEventData += dataContent;
        }
        console.log('[Agent SSE] accumulated currentEventData:', JSON.stringify(currentEventData));
      } else if (line.trim() === '') {
        // 空行（非 data: 行）- 事件结束
        console.log('[Agent SSE] empty line, calling processEvent');
        processEvent();
      }
    }

    fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'text/event-stream',
      },
      body: JSON.stringify(data),
      signal: controller.signal,
    })
      .then(async (response) => {
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        if (!response.body) {
          throw new Error('响应体为空');
        }

        const reader = response.body.getReader();
        const decoder = new TextDecoder();

        while (true) {
          const { done, value } = await reader.read();
          if (done) break;

          const chunk = decoder.decode(value, { stream: true });
          buffer += chunk;

          const lines = buffer.split('\n');
          buffer = lines.pop() || '';

          for (const line of lines) {
            processSSELine(line);
          }
        }

        if (buffer.trim()) {
          processSSELine(buffer);
        }

        console.log('Agent流式读取完成，总长度:', fullContent.length);
        onDone(fullContent);
      })
      .catch((error) => {
        if (error.name === 'AbortError') {
          console.log('Agent流式请求被取消');
          onDone(fullContent);
        } else {
          console.error('Agent流式请求失败:', error);
          onError(error.message || '流式生成失败');
        }
      });

    return () => {
      console.log('取消Agent流式请求');
      controller.abort();
    };
  },

  // 流式生成 Skill 提示词
  generateSkillStream(
    data: SkillPromptRequest,
    onMessage: (content: string) => void,
    onDone: (fullContent: string) => void,
    onError: (error: string) => void
  ): () => void {
    const apiUrl = import.meta.env.VITE_API_BASE_URL || '/api';
    const url = `${apiUrl}/generate-skill/stream`;

    console.log('开始Skill流式请求:', url);

    const controller = new AbortController();
    let fullContent = '';
    let buffer = '';

    // SSE 解析状态
    let currentEventName = '';
    let currentEventData = '';

    function processEvent() {
      if (!currentEventData) return;

      console.log('[Skill SSE] processEvent: currentEventName=', currentEventName, 'currentEventData=', currentEventData);

      if (currentEventName === 'message') {
        try {
          const parsed = JSON.parse(currentEventData);
          console.log('[Skill SSE] parsed content:', parsed.content);
          if (parsed.content) {
            fullContent += parsed.content;
            onMessage(parsed.content);
          }
        } catch (e) {
          // JSON 解析失败，使用原始文本（SSE data 内容本身可能包含换行符）
          console.error('[Skill SSE] JSON parse error:', e, 'data:', currentEventData);
          fullContent += currentEventData;
          onMessage(currentEventData);
        }
      } else if (currentEventName === 'error') {
        onError(currentEventData);
      }

      currentEventData = '';
      currentEventName = '';
    }

    function processSSELine(line: string) {
      // Debug: show raw line
      console.log('[Skill SSE] raw line:', JSON.stringify(line));

      if (line.startsWith('event:')) {
        // 如果有累积的数据，先处理之前的事件
        if (currentEventData) {
          processEvent();
        }
        currentEventName = line.substring(6).trim();
        console.log('[Skill SSE] event name:', currentEventName);
      } else if (line.startsWith('data:')) {
        // 提取 data 内容
        const dataContent = line.substring(5); // 不 trim，保留原始内容
        console.log('[Skill SSE] data content:', JSON.stringify(dataContent));

        if (dataContent === '') {
          // 空 data: 表示内容中的空行，添加换行符
          currentEventData += '\n';
        } else {
          currentEventData += dataContent;
        }
        console.log('[Skill SSE] accumulated currentEventData:', JSON.stringify(currentEventData));
      } else if (line.trim() === '') {
        // 空行（非 data: 行）- 事件结束
        console.log('[Skill SSE] empty line, calling processEvent');
        processEvent();
      }
    }

    fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'text/event-stream',
      },
      body: JSON.stringify(data),
      signal: controller.signal,
    })
      .then(async (response) => {
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }

        if (!response.body) {
          throw new Error('响应体为空');
        }

        const reader = response.body.getReader();
        const decoder = new TextDecoder();

        while (true) {
          const { done, value } = await reader.read();
          if (done) break;

          const chunk = decoder.decode(value, { stream: true });
          buffer += chunk;

          const lines = buffer.split('\n');
          buffer = lines.pop() || '';

          for (const line of lines) {
            processSSELine(line);
          }
        }

        if (buffer.trim()) {
          processSSELine(buffer);
        }

        console.log('Skill流式读取完成，总长度:', fullContent.length);
        onDone(fullContent);
      })
      .catch((error) => {
        if (error.name === 'AbortError') {
          console.log('Skill流式请求被取消');
          onDone(fullContent);
        } else {
          console.error('Skill流式请求失败:', error);
          onError(error.message || '流式生成失败');
        }
      });

    return () => {
      console.log('取消Skill流式请求');
      controller.abort();
    };
  },

  // 保存Agent提示词到历史记录
  async saveAgent(data: SaveAgentRequest): Promise<number> {
    const response = await request.post<ApiResponse<number>>('/history/agent', data);
    if (!response.data.success) {
      throw new Error(response.data.message || '保存失败');
    }
    return response.data.data;
  },

  // 保存Skill提示词到历史记录
  async saveSkill(data: SaveSkillRequest): Promise<number> {
    const response = await request.post<ApiResponse<number>>('/history/skill', data);
    if (!response.data.success) {
      throw new Error(response.data.message || '保存失败');
    }
    return response.data.data;
  },

  // 健康检查
  health(): Promise<{ status: string }> {
    return request.get<ApiResponse<{ status: string }>>('/health')
      .then(res => res.data.data);
  }
};
