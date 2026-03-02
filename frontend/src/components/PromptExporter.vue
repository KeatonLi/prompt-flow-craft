<template>
  <div class="exporter">
    <el-dropdown trigger="click" @command="handleExport">
      <el-button size="small" type="success">
        <el-icon><Download /></el-icon>
        导出
        <el-icon class="el-icon--right"><ArrowDown /></el-icon>
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="markdown">
            <el-icon><Document /></el-icon>
            Markdown (.md)
          </el-dropdown-item>
          <el-dropdown-item command="json">
            <el-icon><Document /></el-icon>
            JSON (.json)
          </el-dropdown-item>
          <el-dropdown-item command="txt">
            <el-icon><Document /></el-icon>
            纯文本 (.txt)
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script>
import { Download, ArrowDown, Document } from '@element-plus/icons-vue'

export default {
  name: 'PromptExporter',
  components: {
    Download,
    ArrowDown,
    Document
  },
  props: {
    prompt: {
      type: String,
      required: true
    },
    title: {
      type: String,
      default: '提示词'
    }
  },
  methods: {
    handleExport(format) {
      let content = ''
      let filename = ''
      let mimeType = ''

      const timestamp = new Date().toISOString().slice(0, 10)

      switch (format) {
        case 'markdown':
          content = this.generateMarkdown()
          filename = `${this.title}_${timestamp}.md`
          mimeType = 'text/markdown'
          break
        case 'json':
          content = this.generateJSON()
          filename = `${this.title}_${timestamp}.json`
          mimeType = 'application/json'
          break
        case 'txt':
          content = this.prompt
          filename = `${this.title}_${timestamp}.txt`
          mimeType = 'text/plain'
          break
      }

      this.downloadFile(content, filename, mimeType)
      this.$message.success(`已导出为 ${format.toUpperCase()} 格式`)
    },

    generateMarkdown() {
      return `# ${this.title}

> 生成时间: ${new Date().toLocaleString('zh-CN')}

## 提示词内容

${this.prompt}

---

*由 Prompt Flow Craft 生成*
`
    },

    generateJSON() {
      const data = {
        title: this.title,
        content: this.prompt,
        createdAt: new Date().toISOString(),
        tool: 'Prompt Flow Craft',
        version: '1.0.0'
      }
      return JSON.stringify(data, null, 2)
    },

    downloadFile(content, filename, mimeType) {
      const blob = new Blob([content], { type: mimeType })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = filename
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)
    }
  }
}
</script>

<style scoped>
.exporter {
  display: inline-block;
}
</style>
