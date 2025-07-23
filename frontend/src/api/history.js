import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// 获取所有历史记录
export const getAllHistory = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/api/history/all`)
    return response.data
  } catch (error) {
    console.error('获取历史记录失败:', error)
    throw error
  }
}

// 获取最近的历史记录
export const getRecentHistory = async (limit = 20) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/api/history/recent`, {
      params: { limit }
    })
    return response.data
  } catch (error) {
    console.error('获取最近历史记录失败:', error)
    throw error
  }
}

// 根据ID获取历史记录详情
export const getHistoryById = async (id) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/api/history/${id}`)
    return response.data
  } catch (error) {
    console.error('获取历史记录详情失败:', error)
    throw error
  }
}

// 搜索历史记录
export const searchHistory = async (keyword) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/api/history/search`, {
      params: { keyword }
    })
    return response.data
  } catch (error) {
    console.error('搜索历史记录失败:', error)
    throw error
  }
}