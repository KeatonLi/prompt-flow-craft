import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: `${import.meta.env.VITE_API_BASE_URL}/api`,
  timeout: 30000, // 30秒超时
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    console.log('发送请求:', config)
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    console.log('收到响应:', response)
    return response.data
  },
  error => {
    console.error('响应错误:', error)
    
    // 处理不同类型的错误
    if (error.response) {
      // 服务器返回错误状态码
      const { status, data } = error.response
      switch (status) {
        case 400:
          throw new Error(data.message || '请求参数错误')
        case 500:
          throw new Error(data.message || '服务器内部错误')
        default:
          throw new Error(data.message || `请求失败 (${status})`)
      }
    } else if (error.request) {
      // 网络错误
      throw new Error('网络连接失败，请检查网络设置')
    } else {
      // 其他错误
      throw new Error(error.message || '未知错误')
    }
  }
)

/**
 * 生成提示词API
 * @param {Object} promptRequest - 提示词请求对象
 * @param {string} promptRequest.taskDescription - 任务描述
 * @param {string} promptRequest.targetAudience - 目标受众
 * @param {string} promptRequest.outputFormat - 输出格式
 * @param {string} promptRequest.constraints - 约束条件
 * @param {string} promptRequest.examples - 参考示例
 * @param {string} promptRequest.tone - 语调风格
 * @param {string} promptRequest.length - 内容长度
 * @returns {Promise<Object>} 生成结果
 */
export const generatePromptAPI = async (promptRequest) => {
  try {
    const response = await api.post('/generate-prompt', {
      task_description: promptRequest.taskDescription,
      target_audience: promptRequest.targetAudience,
      output_format: promptRequest.outputFormat,
      constraints: promptRequest.constraints,
      examples: promptRequest.examples,
      tone: promptRequest.tone,
      length: promptRequest.length
    })
    
    return response
  } catch (error) {
    console.error('生成提示词API调用失败:', error)
    throw error
  }
}

/**
 * 健康检查API
 * @returns {Promise<string>} 健康状态
 */
export const healthCheckAPI = async () => {
  try {
    const response = await api.get('/health')
    return response
  } catch (error) {
    console.error('健康检查API调用失败:', error)
    throw error
  }
}

export default api