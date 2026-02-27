import request from './request';
import type { ApiResponse, PromptRequest } from '@/types';

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
          
          // 解码数据
          const chunk = decoder.decode(value, { stream: true });
          buffer += chunk;
          
          // 处理 SSE 格式的数据
          // SSE 格式: data: xxx\n\n 或 event: xxx\ndata: xxx\n\n
          const lines = buffer.split('\n');
          buffer = lines.pop() || ''; // 保留未完成的行
          
          let currentData: string | null = null;
          
          for (let i = 0; i < lines.length; i++) {
            const line = lines[i].trim();
            
            if (line.startsWith('data:')) {
              currentData = line.substring(5).trim();
            } else if (line === '' && currentData !== null) {
              // 空行表示一个事件结束，处理当前数据
              processSSEData(currentData);
              currentData = null;
            }
          }
          
          // 如果最后还有未处理的数据
          if (currentData !== null && lines[lines.length - 1] === '') {
            processSSEData(currentData);
          }
        }
        
        // 处理缓冲区中剩余的数据
        if (buffer.trim()) {
          const lines = buffer.split('\n');
          for (const line of lines) {
            const trimmed = line.trim();
            if (trimmed.startsWith('data:')) {
              const data = trimmed.substring(5).trim();
              processSSEData(data);
            }
          }
        }
        
        // 完成
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
    
    // 处理单个 SSE 数据
    function processSSEData(data: string) {
      if (!data || data === '[DONE]') {
        if (data === '[DONE]') {
          console.log('收到 [DONE] 标记');
        }
        return;
      }
      
      try {
        const parsed = JSON.parse(data);
        
        if (parsed.content !== undefined) {
          // 流式内容片段
          const content = parsed.content;
          if (content) {
            fullContent += content;
            onMessage(content);
          }
        } else if (parsed.done) {
          // 完成标记
          console.log('收到 done 事件');
          if (parsed.fullContent) {
            fullContent = parsed.fullContent;
          }
        } else if (parsed.error) {
          // 错误
          console.error('收到错误:', parsed.error);
          onError(parsed.error);
        }
      } catch (e) {
        // 如果不是 JSON，直接当作纯文本内容
        console.debug('非JSON数据，作为纯文本处理:', data);
        if (data && data !== '[DONE]') {
          fullContent += data;
          onMessage(data);
        }
      }
    }
    
    // 返回取消函数
    return () => {
      console.log('取消流式请求');
      controller.abort();
    };
  },

  // 健康检查
  health(): Promise<{ status: string }> {
    return request.get<ApiResponse<{ status: string }>>('/health')
      .then(res => res.data.data);
  }
};
