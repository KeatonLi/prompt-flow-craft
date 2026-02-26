import axios, { AxiosInstance, AxiosError, AxiosResponse } from 'axios';
import type { ApiResponse } from '@/types';

// 创建 axios 实例
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 可以在这里添加 token 等
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse<any>>) => {
    const { data } = response;
    if (!data.success) {
      // 业务错误
      console.error('API Error:', data.message);
      return Promise.reject(new Error(data.message));
    }
    return response;
  },
  (error: AxiosError) => {
    // HTTP 错误
    let message = '网络错误';
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求参数错误';
          break;
        case 404:
          message = '资源不存在';
          break;
        case 500:
          message = '服务器内部错误';
          break;
        default:
          message = `请求失败: ${error.response.status}`;
      }
    } else if (error.request) {
      message = '无法连接到服务器';
    }
    console.error('Request Error:', message);
    return Promise.reject(new Error(message));
  }
);

export default request;
