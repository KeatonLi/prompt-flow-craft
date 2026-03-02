<template>
  <div class="compare">
    <div class="container">
      <div class="header">
        <h1 class="title">
          <el-icon class="title-icon"><Connection /></el-icon>
          提示词对比
        </h1>
        <p class="subtitle">对比两个提示词的差异，找到更好的改进方向</p>
      </div>

      <el-row :gutter="24">
        <el-col :span="12">
          <el-card class="input-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="label-a">提示词 A</span>
                <el-tag type="info" v-if="result">评分: {{ result.scoreA }}分</el-tag>
              </div>
            </template>
            <el-input
              v-model="promptA"
              type="textarea"
              :rows="10"
              placeholder="请输入提示词A..."
              show-word-limit
              maxlength="2000"
            />
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card class="input-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span class="label-b">提示词 B</span>
                <el-tag type="info" v-if="result">评分: {{ result.scoreB }}分</el-tag>
              </div>
            </template>
            <el-input
              v-model="promptB"
              type="textarea"
              :rows="10"
              placeholder="请输入提示词B..."
              show-word-limit
              maxlength="2000"
            />
          </el-card>
        </el-col>
      </el-row>

      <div class="action-area">
        <el-button 
          type="primary" 
          size="large" 
          :loading="loading"
          @click="comparePrompts"
          class="compare-btn"
        >
          <el-icon><Connection /></el-icon>
          {{ loading ? '对比分析中...' : '开始对比' }}
        </el-button>
        <el-button size="large" @click="swapPrompts" class="swap-btn">
          <el-icon><Sort /></el-icon>
          交换
        </el-button>
        <el-button size="large" @click="clearAll" class="clear-btn">
          <el-icon><Delete /></el-icon>
          清空
        </el-button>
      </div>

      <!-- 对比结果 -->
      <el-card v-if="result" class="result-card" shadow="hover">
        <template #header>
          <div class="result-header">
            <span>对比结果</span>
            <el-tag :type="winnerType" size="large" class="winner-tag">
              获胜者: {{ winnerText }}
            </el-tag>
          </div>
        </template>
        
        <!-- 分数对比条 -->
        <div class="score-comparison">
          <div class="score-item">
            <span class="score-label">提示词 A</span>
            <el-progress 
              :percentage="result.scoreA" 
              :color="scoreColor(result.scoreA)"
              :stroke-width="12"
              striped
              striped-flow
            />
            <span class="score-value">{{ result.scoreA }}分</span>
          </div>
          <div class="vs">VS</div>
          <div class="score-item">
            <span class="score-label">提示词 B</span>
            <el-progress 
              :percentage="result.scoreB" 
              :color="scoreColor(result.scoreB)"
              :stroke-width="12"
              striped
              striped-flow
            />
            <span class="score-value">{{ result.scoreB }}分</span>
          </div>
        </div>

        <!-- 详细对比项 -->
        <div class="comparison-details" v-if="result.comparisons && result.comparisons.length">
          <h3>各项指标对比</h3>
          <el-row :gutter="16">
            <el-col :span="24" v-for="(item, index) in result.comparisons" :key="index">
              <div class="comparison-item">
                <div class="item-header">
                  <span class="item-aspect">{{ item.aspect }}</span>
                  <el-tag :type="item.winner === 'A' ? 'success' : item.winner === 'B' ? 'warning' : 'info'" size="small">
                    {{ item.winner === 'A' ? 'A胜' : item.winner === 'B' ? 'B胜' : '平局' }}
                  </el-tag>
                </div>
                <div class="item-scores">
                  <span class="score-a">A: {{ item.scoreA }}分</span>
                  <span class="score-b">B: {{ item.scoreB }}分</span>
                </div>
                <p class="item-analysis">{{ item.analysis }}</p>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- AI分析总结 -->
        <div class="ai-analysis" v-if="result.summary">
          <h3>📊 AI分析总结</h3>
          <div class="summary-content">
            <pre>{{ result.summary }}</pre>
          </div>
        </div>

        <!-- 改进建议 -->
        <div class="suggestions" v-if="result.suggestions && result.suggestions.length">
          <h3>💡 改进建议</h3>
          <ul>
            <li v-for="(suggestion, index) in result.suggestions" :key="index">
              {{ suggestion }}
            </li>
          </ul>
        </div>
      </el-card>

      <!-- 示例提示词 -->
      <el-card class="examples-card" shadow="hover">
        <template #header>
          <span>💡 示例提示词（点击快速填充）</span>
        </template>
        <div class="examples">
          <div class="example-item" @click="loadExample(1)">
            <div class="example-title">客服对话 vs 详细客服</div>
            <div class="example-desc">对比简单和详细的客服提示词</div>
          </div>
          <div class="example-item" @click="loadExample(2)">
            <div class="example-title">代码生成 vs 结构化代码</div>
            <div class="example-desc">对比普通和结构化的代码提示词</div>
          </div>
          <div class="example-item" @click="loadExample(3)">
            <div class="example-title">文章写作 vs 专业写作</div>
            <div class="example-desc">对比基础和专业级别的写作提示词</div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { Connection, Sort, Delete } from '@element-plus/icons-vue'
import request from '@/api/request'

export default {
  name: 'CompareView',
  components: {
    Connection,
    Sort,
    Delete
  },
  data() {
    return {
      loading: false,
      promptA: '',
      promptB: '',
      result: null
    }
  },
  computed: {
    winnerType() {
      if (!this.result) return 'info'
      if (this.result.winner === 'A') return 'success'
      if (this.result.winner === 'B') return 'warning'
      return 'info'
    },
    winnerText() {
      if (!this.result) return '-'
      if (this.result.winner === 'A') return '提示词 A'
      if (this.result.winner === 'B') return '提示词 B'
      return '平局'
    }
  },
  methods: {
    async comparePrompts() {
      if (!this.promptA.trim()) {
        this.$message.warning('请输入提示词A')
        return
      }
      if (!this.promptB.trim()) {
        this.$message.warning('请输入提示词B')
        return
      }
      
      this.loading = true
      try {
        const response = await request.post('/api/prompt/compare', {
          promptA: this.promptA,
          promptB: this.promptB
        })
        
        if (response.data.code === 200) {
          this.result = response.data.data
          this.$message.success('对比完成！')
        } else {
          this.$message.error(response.data.message || '对比失败')
        }
      } catch (error) {
        console.error('对比失败:', error)
        this.$message.error('对比失败，请稍后重试')
      } finally {
        this.loading = false
      }
    },
    
    swapPrompts() {
      const temp = this.promptA
      this.promptA = this.promptB
      this.promptB = temp
      this.result = null
    },
    
    clearAll() {
      this.promptA = ''
      this.promptB = ''
      this.result = null
      this.$message.info('已清空')
    },
    
    scoreColor(score) {
      if (score >= 80) return '#67c23a'
      if (score >= 60) return '#409eff'
      if (score >= 40) return '#e6a23c'
      return '#f56c6c'
    },
    
    loadExample(type) {
      switch(type) {
        case 1:
          this.promptA = '你是客服，回复用户问题。'
          this.promptB = '你是一位专业的电商客服助手，拥有5年以上的客服经验。你的职责是：\n\n1. 礼貌热情地回复客户咨询\n2. 了解客户需求，提供针对性解决方案\n3. 遇到无法解决的问题时，引导客户联系人工客服\n4. 适当使用表情拉近距离\n\n回复要求：\n- 使用简洁清晰的语言\n- 控制在100字以内\n- 如需等待，请说明预计时间'
          break
        case 2:
          this.promptA = '写一个排序算法'
          this.promptB = '你是一位Python后端工程师。请帮我实现一个高效的排序算法。\n\n要求：\n1. 使用Python语言\n2. 实现快速排序算法\n3. 时间复杂度要求O(n log n)\n4. 代码需要包含完整的类型注解\n5. 添加详细的注释说明\n6. 提供测试用例验证正确性\n\n输出格式：代码块 + 测试结果'
          break
        case 3:
          this.promptA = '写一篇关于AI的文章'
          this.promptB = '你是一位科技专栏作家，擅长写深入浅出的技术文章。\n\n任务：撰写一篇关于"AI提示词工程"的文章\n\n目标读者：对AI感兴趣但非技术背景的普通用户\n\n文章要求：\n- 标题吸引人\n- 结构清晰，包含引言、主体、结论\n- 使用通俗易懂的比喻解释概念\n- 包含2-3个实际案例\n- 篇幅：1500-2000字\n\n风格：\n- 正式但不失活泼\n- 避免过多专业术语\n- 适当使用小标题分隔章节'
          break
      }
      this.$message.success('已加载示例')
    }
  }
}
</script>

<style scoped>
.compare {
  min-height: calc(100vh - 144px);
  background: 
    radial-gradient(circle at 20% 20%, rgba(99, 102, 241, 0.08) 0%, transparent 40%),
    radial-gradient(circle at 80% 80%, rgba(168, 85, 247, 0.08) 0%, transparent 40%),
    linear-gradient(135deg, #ffffff 0%, #f8fafc 50%, #f1f5f9 100%);
  padding: 40px 20px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  text-align: center;
  margin-bottom: 40px;
}

.title {
  font-size: 2.5rem;
  background: linear-gradient(135deg, #1e293b 0%, #334155 50%, #3b82f6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  font-weight: 800;
}

.title-icon {
  font-size: 2.8rem;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 1.25rem;
  color: #64748b;
  margin: 0;
  font-weight: 500;
}

.input-card {
  margin-bottom: 24px;
  border: 1px solid rgba(226, 232, 240, 0.6);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(24px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.input-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.label-a {
  color: #3b82f6;
  font-size: 1.1rem;
}

.label-b {
  color: #8b5cf6;
  font-size: 1.1rem;
}

.action-area {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-bottom: 40px;
}

.compare-btn {
  padding: 16px 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(59, 130, 246, 0.25);
}

.compare-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.35);
}

.swap-btn, .clear-btn {
  padding: 16px 24px;
  border-radius: 12px;
}

.result-card {
  margin-bottom: 40px;
  border: none;
  border-radius: 20px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  position: relative;
}

.result-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #3b82f6, #8b5cf6, #10b981);
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 1.2rem;
  color: #333;
}

.winner-tag {
  font-size: 1rem;
  padding: 8px 16px;
}

.score-comparison {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40px;
  padding: 30px 0;
  margin: 20px 0;
  background: #f8fafc;
  border-radius: 12px;
}

.score-item {
  flex: 1;
  max-width: 300px;
  text-align: center;
}

.score-label {
  display: block;
  font-weight: 600;
  margin-bottom: 12px;
  font-size: 1rem;
  color: #64748b;
}

.score-value {
  display: block;
  margin-top: 12px;
  font-weight: 700;
  font-size: 1.2rem;
  color: #333;
}

.vs {
  font-size: 1.5rem;
  font-weight: 800;
  color: #94a3b8;
}

.comparison-details {
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid #e2e8f0;
}

.comparison-details h3 {
  font-size: 1.2rem;
  color: #333;
  margin-bottom: 20px;
}

.comparison-item {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  border: 1px solid #e2e8f0;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.item-aspect {
  font-weight: 600;
  color: #333;
}

.item-scores {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
  font-size: 0.9rem;
}

.score-a {
  color: #3b82f6;
}

.score-b {
  color: #8b5cf6;
}

.item-analysis {
  color: #64748b;
  font-size: 0.9rem;
  margin: 0;
  line-height: 1.6;
}

.ai-analysis {
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid #e2e8f0;
}

.ai-analysis h3 {
  font-size: 1.2rem;
  color: #333;
  margin-bottom: 16px;
}

.summary-content {
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #bae6fd;
}

.summary-content pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: inherit;
  line-height: 1.8;
  color: #0c4a6e;
  font-size: 0.95rem;
}

.suggestions {
  margin-top: 30px;
  padding-top: 30px;
  border-top: 1px solid #e2e8f0;
}

.suggestions h3 {
  font-size: 1.2rem;
  color: #333;
  margin-bottom: 16px;
}

.suggestions ul {
  margin: 0;
  padding-left: 20px;
}

.suggestions li {
  color: #475569;
  line-height: 2;
  font-size: 0.95rem;
}

.examples-card {
  border: 1px solid rgba(226, 232, 240, 0.6);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.95);
}

.examples {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 16px;
}

.example-item {
  padding: 16px;
  background: #f8fafc;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.example-item:hover {
  background: #fff;
  border-color: #3b82f6;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

.example-title {
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.example-desc {
  font-size: 0.9rem;
  color: #64748b;
}

@media (max-width: 768px) {
  .compare {
    padding: 20px 12px;
  }
  
  .title {
    font-size: 1.8rem;
  }
  
  .el-col {
    margin-bottom: 16px;
  }
  
  .score-comparison {
    flex-direction: column;
    gap: 20px;
  }
  
  .action-area {
    flex-wrap: wrap;
  }
}
</style>
