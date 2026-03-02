<template>
  <div class="community-page">
    <div class="community-header">
      <h1>🌟 提示词社区</h1>
      <p>发现并分享优质的 AI 提示词</p>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <input 
        v-model="searchKeyword" 
        type="text" 
        placeholder="搜索社区提示词..." 
        @keyup.enter="searchPrompts"
      />
      <button @click="searchPrompts">搜索</button>
    </div>

    <!-- 标签页切换 -->
    <div class="tabs">
      <button 
        :class="{ active: currentTab === 'latest' }" 
        @click="switchTab('latest')"
      >
        最新分享
      </button>
      <button 
        :class="{ active: currentTab === 'popular' }" 
        @click="switchTab('popular')"
      >
        热门推荐
      </button>
      <button 
        :class="{ active: currentTab === 'share' }" 
        @click="openShareModal"
      >
        分享提示词
      </button>
    </div>

    <!-- 提示词列表 -->
    <div class="prompts-grid">
      <div 
        v-for="prompt in prompts" 
        :key="prompt.id" 
        class="prompt-card"
        @click="viewPrompt(prompt)"
      >
        <div class="prompt-header">
          <h3>{{ prompt.title }}</h3>
          <span v-if="prompt.category" class="category-tag" :style="{ backgroundColor: prompt.category.color }">
            {{ prompt.category.name }}
          </span>
        </div>
        <p class="prompt-desc">{{ prompt.description || '暂无描述' }}</p>
        <div class="prompt-content">
          {{ prompt.content.substring(0, 150) }}{{ prompt.content.length > 150 ? '...' : '' }}
        </div>
        <div class="prompt-footer">
          <span class="author">👤 {{ prompt.authorName }}</span>
          <span class="stats">
            <span>👍 {{ prompt.likes }}</span>
            <span>👁 {{ prompt.views }}</span>
          </span>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="totalPages > 1">
      <button 
        :disabled="currentPage === 0" 
        @click="loadPage(currentPage - 1)"
      >
        上一页
      </button>
      <span>{{ currentPage + 1 }} / {{ totalPages }}</span>
      <button 
        :disabled="currentPage >= totalPages - 1" 
        @click="loadPage(currentPage + 1)"
      >
        下一页
      </button>
    </div>

    <!-- 提示词详情弹窗 -->
    <div v-if="selectedPrompt" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <button class="close-btn" @click="closeModal">×</button>
        <h2>{{ selectedPrompt.title }}</h2>
        <div class="modal-meta">
          <span>👤 {{ selectedPrompt.authorName }}</span>
          <span>👍 {{ selectedPrompt.likes }}</span>
          <span>👁 {{ selectedPrompt.views }}</span>
        </div>
        <div class="prompt-full-content">
          <pre>{{ selectedPrompt.content }}</pre>
        </div>
        <div class="modal-actions">
          <button class="like-btn" @click="likePrompt(selectedPrompt.id)">
            👍 点赞 ({{ selectedPrompt.likes }})
          </button>
          <button class="copy-btn" @click="copyContent(selectedPrompt.content)">
            📋 复制
          </button>
        </div>
      </div>
    </div>

    <!-- 分享弹窗 -->
    <div v-if="showShareModal" class="modal-overlay" @click="closeShareModal">
      <div class="modal-content share-modal" @click.stop>
        <button class="close-btn" @click="closeShareModal">×</button>
        <h2>分享提示词到社区</h2>
        <div class="form-group">
          <label>标题 *</label>
          <input v-model="shareForm.title" type="text" placeholder="输入提示词标题" />
        </div>
        <div class="form-group">
          <label>描述</label>
          <input v-model="shareForm.description" type="text" placeholder="简短描述（可选）" />
        </div>
        <div class="form-group">
          <label>作者名称 *</label>
          <input v-model="shareForm.authorName" type="text" placeholder="你的名字" />
        </div>
        <div class="form-group">
          <label>提示词内容 *</label>
          <textarea v-model="shareForm.content" rows="10" placeholder="输入你的提示词内容..."></textarea>
        </div>
        <div class="form-group">
          <label>分类</label>
          <select v-model="shareForm.categoryId">
            <option :value="null">无分类</option>
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.name }}
            </option>
          </select>
        </div>
        <button class="submit-btn" @click="submitShare">发布分享</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080/api'

const prompts = ref<any[]>([])
const currentPage = ref(0)
const totalPages = ref(0)
const currentTab = ref('latest')
const searchKeyword = ref('')
const selectedPrompt = ref<any>(null)
const showShareModal = ref(false)
const categories = ref<any[]>([])

const shareForm = ref({
  title: '',
  description: '',
  authorName: '',
  content: '',
  categoryId: null as number | null
})

const loadPrompts = async (page = 0, sortBy = 'latest') => {
  try {
    const url = `${API_BASE}/community/prompts?page=${page}&size=12&sortBy=${sortBy}`
    const response = await fetch(url)
    const data = await response.json()
    if (data.code === 200) {
      prompts.value = data.data.content
      totalPages.value = data.data.totalPages
      currentPage.value = data.data.number
    }
  } catch (error) {
    console.error('加载提示词失败:', error)
  }
}

const loadCategories = async () => {
  try {
    const response = await fetch(`${API_BASE}/categories`)
    const data = await response.json()
    if (data.code === 200) {
      categories.value = data.data
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const switchTab = (tab: string) => {
  currentTab.value = tab
  if (tab !== 'share') {
    loadPrompts(0, tab)
  }
}

const loadPage = (page: number) => {
  loadPrompts(page, currentTab.value === 'popular' ? 'likes' : 'latest')
}

const searchPrompts = async () => {
  if (!searchKeyword.value.trim()) {
    loadPrompts(0, currentTab.value)
    return
  }
  try {
    const response = await fetch(`${API_BASE}/community/prompts/search?keyword=${encodeURIComponent(searchKeyword.value)}&page=0&size=12`)
    const data = await response.json()
    if (data.code === 200) {
      prompts.value = data.data.content
      totalPages.value = data.data.totalPages
    }
  } catch (error) {
    console.error('搜索失败:', error)
  }
}

const viewPrompt = (prompt: any) => {
  selectedPrompt.value = prompt
}

const closeModal = () => {
  selectedPrompt.value = null
}

const likePrompt = async (id: number) => {
  try {
    const response = await fetch(`${API_BASE}/community/prompts/${id}/like`, {
      method: 'POST'
    })
    const data = await response.json()
    if (data.code === 200) {
      // Update local state
      const prompt = prompts.value.find(p => p.id === id)
      if (prompt) {
        prompt.likes++
      }
      if (selectedPrompt.value && selectedPrompt.value.id === id) {
        selectedPrompt.value.likes++
      }
    }
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

const copyContent = async (content: string) => {
  try {
    await navigator.clipboard.writeText(content)
    alert('复制成功！')
  } catch (error) {
    console.error('复制失败:', error)
  }
}

const openShareModal = () => {
  showShareModal.value = true
}

const closeShareModal = () => {
  showShareModal.value = false
  shareForm.value = {
    title: '',
    description: '',
    authorName: '',
    content: '',
    categoryId: null
  }
}

const submitShare = async () => {
  if (!shareForm.value.title || !shareForm.value.content || !shareForm.value.authorName) {
    alert('请填写标题、内容和作者名称')
    return
  }
  
  try {
    const response = await fetch(`${API_BASE}/community/prompts`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(shareForm.value)
    })
    const data = await response.json()
    if (data.code === 200) {
      alert('分享成功！')
      closeShareModal()
      loadPrompts(0, 'latest')
    } else {
      alert(data.message || '分享失败')
    }
  } catch (error) {
    console.error('分享失败:', error)
    alert('分享失败，请稍后重试')
  }
}

onMounted(() => {
  loadPrompts()
  loadCategories()
})
</script>

<style scoped>
.community-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.community-header {
  text-align: center;
  margin-bottom: 30px;
}

.community-header h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.community-header p {
  color: #666;
  font-size: 1.1rem;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-bar input {
  flex: 1;
  padding: 12px 20px;
  border: 2px solid #e0e0e0;
  border-radius: 25px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.search-bar input:focus {
  outline: none;
  border-color: #667eea;
}

.search-bar button {
  padding: 12px 30px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1rem;
  transition: transform 0.2s;
}

.search-bar button:hover {
  transform: scale(1.05);
}

.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
}

.tabs button {
  padding: 10px 20px;
  background: #f5f5f5;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s;
}

.tabs button.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.tabs button:hover:not(.active) {
  background: #e0e0e0;
}

.prompts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.prompt-card {
  background: white;
  border-radius: 15px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.prompt-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 20px rgba(0,0,0,0.15);
}

.prompt-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.prompt-header h3 {
  font-size: 1.2rem;
  margin: 0;
  flex: 1;
}

.category-tag {
  padding: 4px 12px;
  border-radius: 15px;
  color: white;
  font-size: 0.8rem;
  margin-left: 10px;
}

.prompt-desc {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 15px;
}

.prompt-content {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 10px;
  font-size: 0.85rem;
  color: #444;
  margin-bottom: 15px;
  max-height: 100px;
  overflow: hidden;
  white-space: pre-wrap;
  word-break: break-word;
}

.prompt-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #888;
  font-size: 0.9rem;
}

.stats {
  display: flex;
  gap: 15px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
}

.pagination button {
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.3s;
}

.pagination button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.pagination button:hover:not(:disabled) {
  background: #5a6fd6;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 20px;
  padding: 30px;
  max-width: 700px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
}

.close-btn {
  position: absolute;
  top: 15px;
  right: 20px;
  background: none;
  border: none;
  font-size: 2rem;
  cursor: pointer;
  color: #888;
}

.modal-content h2 {
  margin-bottom: 20px;
}

.modal-meta {
  display: flex;
  gap: 20px;
  color: #666;
  margin-bottom: 20px;
}

.prompt-full-content {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 20px;
  max-height: 400px;
  overflow-y: auto;
}

.prompt-full-content pre {
  white-space: pre-wrap;
  word-break: break-word;
  margin: 0;
  font-family: inherit;
}

.modal-actions {
  display: flex;
  gap: 15px;
}

.like-btn, .copy-btn {
  padding: 12px 25px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 1rem;
  transition: transform 0.2s;
}

.like-btn {
  background: #ff6b6b;
  color: white;
}

.copy-btn {
  background: #4ecdc4;
  color: white;
}

.like-btn:hover, .copy-btn:hover {
  transform: scale(1.05);
}

.share-modal {
  max-width: 600px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
}

.form-group textarea {
  resize: vertical;
  font-family: inherit;
}

.submit-btn {
  width: 100%;
  padding: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 1.1rem;
  transition: transform 0.2s;
}

.submit-btn:hover {
  transform: scale(1.02);
}
</style>
