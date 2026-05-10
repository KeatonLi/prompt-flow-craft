<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      <div class="modal-header">
        <h3>从历史记录选择</h3>
        <button class="close-btn" @click="$emit('close')">×</button>
      </div>
      <div class="modal-body">
        <div class="search-box">
          <input v-model="keyword" type="text" placeholder="搜索历史记录..." />
        </div>
        <div class="history-list" v-if="!loading">
          <div
            v-for="record in filteredRecords"
            :key="record.id"
            class="history-item"
            @click="handleSelect(record)"
          >
            <div class="item-header">
              <span class="item-task">{{ record.taskDescription }}</span>
              <span class="item-time">{{ formatTime(record.createdAt) }}</span>
            </div>
            <div class="item-preview">{{ record.generatedPrompt.substring(0, 100) }}...</div>
          </div>
          <div v-if="filteredRecords.length === 0" class="empty-state">
            暂无历史记录
          </div>
        </div>
        <div v-else class="loading-state">加载中...</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import type { PromptRecord } from '@/types';
import { historyApi } from '@/api/history';

const emit = defineEmits<{
  (e: 'close'): void;
  (e: 'select', record: PromptRecord): void;
}>();

const loading = ref(false);
const keyword = ref('');
const records = ref<PromptRecord[]>([]);

onMounted(async () => {
  loading.value = true;
  try {
    records.value = await historyApi.getAll();
  } finally {
    loading.value = false;
  }
});

const filteredRecords = computed(() => {
  if (!keyword.value) return records.value;
  const kw = keyword.value.toLowerCase();
  return records.value.filter(r =>
    r.taskDescription.toLowerCase().includes(kw) ||
    r.generatedPrompt.toLowerCase().includes(kw)
  );
});

const formatTime = (time: string) => {
  const date = new Date(time);
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' });
};

const handleSelect = (record: PromptRecord) => {
  emit('select', record);
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1001;
}

.modal-content {
  background: var(--bg-card);
  border-radius: var(--radius-2xl);
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: var(--text-secondary);
}

.modal-body {
  padding: 20px;
}

.search-box input {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-lg);
  font-size: 14px;
  background: var(--bg-primary);
  color: var(--text-primary);
}

.history-list {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-item {
  padding: 16px;
  background: var(--bg-secondary);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all 0.2s;
}

.history-item:hover {
  background: var(--bg-tertiary);
}

.item-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.item-task {
  font-weight: 500;
  font-size: 14px;
}

.item-time {
  font-size: 12px;
  color: var(--text-secondary);
}

.item-preview {
  font-size: 12px;
  color: var(--text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.empty-state, .loading-state {
  text-align: center;
  padding: 40px;
  color: var(--text-secondary);
}
</style>