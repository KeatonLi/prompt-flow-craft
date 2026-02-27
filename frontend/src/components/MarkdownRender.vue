<template>
  <div class="markdown-body" v-html="renderedContent"></div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import MarkdownIt from 'markdown-it';
import hljs from 'highlight.js';
import 'highlight.js/styles/github-dark.css';

const props = defineProps<{
  content: string;
}>();

// 创建 markdown-it 实例
const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: (str: string, lang: string) => {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(str, { language: lang }).value;
      } catch (__) {}
    }
    return hljs.highlightAuto(str).value;
  }
});

// 渲染内容
const renderedContent = computed(() => {
  if (!props.content) return '';
  return md.render(props.content);
});
</script>

<style scoped>
.markdown-body {
  font-size: 14px;
  line-height: 1.7;
  color: #e2e8f0;
}

.markdown-body :deep(h1),
.markdown-body :deep(h2),
.markdown-body :deep(h3),
.markdown-body :deep(h4),
.markdown-body :deep(h5),
.markdown-body :deep(h6) {
  margin-top: 16px;
  margin-bottom: 12px;
  font-weight: 600;
  line-height: 1.3;
  color: #f8fafc;
}

.markdown-body :deep(h1) { font-size: 1.5em; border-bottom: 1px solid #334155; padding-bottom: 8px; }
.markdown-body :deep(h2) { font-size: 1.3em; border-bottom: 1px solid #334155; padding-bottom: 6px; }
.markdown-body :deep(h3) { font-size: 1.15em; }
.markdown-body :deep(h4) { font-size: 1em; }

.markdown-body :deep(p) {
  margin-bottom: 12px;
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  margin-bottom: 12px;
  padding-left: 24px;
}

.markdown-body :deep(li) {
  margin-bottom: 4px;
}

.markdown-body :deep(code) {
  background: rgba(255, 255, 255, 0.1);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'JetBrains Mono', 'Fira Code', monospace;
  font-size: 0.9em;
}

.markdown-body :deep(pre) {
  background: #0f172a;
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
  margin-bottom: 12px;
}

.markdown-body :deep(pre code) {
  background: none;
  padding: 0;
  font-size: 13px;
  line-height: 1.6;
}

.markdown-body :deep(blockquote) {
  border-left: 4px solid #3b82f6;
  margin: 12px 0;
  padding: 8px 16px;
  background: rgba(59, 130, 246, 0.1);
  border-radius: 0 8px 8px 0;
}

.markdown-body :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 12px;
}

.markdown-body :deep(th),
.markdown-body :deep(td) {
  border: 1px solid #334155;
  padding: 8px 12px;
  text-align: left;
}

.markdown-body :deep(th) {
  background: rgba(255, 255, 255, 0.05);
  font-weight: 600;
}

.markdown-body :deep(a) {
  color: #60a5fa;
  text-decoration: none;
}

.markdown-body :deep(a:hover) {
  text-decoration: underline;
}

.markdown-body :deep(hr) {
  border: none;
  border-top: 1px solid #334155;
  margin: 16px 0;
}

.markdown-body :deep(strong) {
  color: #f8fafc;
  font-weight: 600;
}
</style>
