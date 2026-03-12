<template>
  <div class="category-nav">
    <div class="nav-header">
      <div class="nav-title">
        <span class="title-icon">📁</span>
        <span class="title-text">分类</span>
      </div>
      <span class="count-badge">{{ categories.length }}</span>
    </div>

    <nav class="nav-list">
      <button
        class="nav-item"
        :class="{ active: currentCategoryId === null }"
        @click="selectCategory(null)"
      >
        <span class="nav-icon home-icon">🏠</span>
        <span class="nav-text">全部</span>
        <span class="nav-count" :class="{ highlight: totalCount > 0 }">{{ totalCount }}</span>
      </button>

      <button
        v-for="category in categories"
        :key="category.id"
        class="nav-item"
        :class="{ active: currentCategoryId === category.id }"
        :style="getItemStyle(category)"
        @click="selectCategory(category.id)"
      >
        <span class="nav-icon">{{ category.icon }}</span>
        <span class="nav-text">{{ category.name }}</span>
        <span class="nav-count">{{ getCategoryCount(category.id) }}</span>
      </button>
    </nav>

    <div class="nav-divider"></div>

    <div class="nav-section">
      <h4 class="section-title">
        <span class="section-icon">🔥</span>
        热门标签
      </h4>
      <div class="tag-cloud">
        <span
          v-for="tag in hotTags"
          :key="tag.id"
          class="tag-item"
          :style="getTagStyle(tag)"
          @click="selectTag(tag.id)"
        >
          {{ tag.name }}
        </span>
        <span v-if="hotTags.length === 0" class="tags-empty">暂无热门标签</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue';
import { useCategoryStore, useTagStore, useHistoryStore } from '@/stores';

const categoryStore = useCategoryStore();
const tagStore = useTagStore();
const historyStore = useHistoryStore();

const categories = computed(() => categoryStore.sortedCategories);
const currentCategoryId = computed(() => categoryStore.currentCategoryId);
const hotTags = computed(() => tagStore.hotTags.slice(0, 10));
const totalCount = computed(() => categoryStore.totalCount);

// 获取分类数量
function getCategoryCount(categoryId: number): number {
  return categoryStore.getCategoryCount(categoryId);
}

// 获取导航项样式
function getItemStyle(category: any) {
  const isActive = currentCategoryId.value === category.id;
  if (isActive) {
    return {
      '--category-color': category.color,
      '--category-bg': `${category.color}15`,
    };
  }
  return {
    '--category-color': category.color,
  };
}

// 获取标签样式
function getTagStyle(tag: any) {
  return {
    backgroundColor: `${tag.color}15`,
    color: tag.color,
    borderColor: `${tag.color}30`,
  };
}

function selectCategory(id: number | null) {
  categoryStore.setCurrentCategory(id);
  historyStore.setQueryParams({
    categoryId: id ?? undefined,
    page: 1
  });
  historyStore.fetchRecords();
}

function selectTag(tagId: number) {
  tagStore.toggleTagSelection(tagId);
}

onMounted(() => {
  categoryStore.fetchCategories();
  categoryStore.fetchCategoryStats();
  tagStore.fetchHotTags();
});
</script>

<style scoped>
.category-nav {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.nav-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding: 0 4px;
}

.nav-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-icon {
  font-size: 1.1rem;
}

.title-text {
  font-size: 0.9rem;
  font-weight: 600;
  color: #475569;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.count-badge {
  font-size: 0.7rem;
  padding: 3px 10px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  border-radius: 12px;
  font-weight: 600;
}

.nav-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 11px 14px;
  border: none;
  background: transparent;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
  font-size: 0.9rem;
  color: #475569;
  position: relative;
  overflow: hidden;
}

.nav-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%) scaleY(0);
  width: 3px;
  height: 20px;
  background: var(--category-color, #3b82f6);
  border-radius: 0 3px 3px 0;
  transition: transform 0.2s ease;
}

.nav-item:hover {
  background: rgba(59, 130, 246, 0.06);
  color: #3b82f6;
}

.nav-item.active {
  background: var(--category-bg, rgba(59, 130, 246, 0.1));
  color: var(--category-color, #3b82f6);
  font-weight: 600;
}

.nav-item.active::before {
  transform: translateY(-50%) scaleY(1);
}

.nav-icon {
  font-size: 1.1rem;
  width: 24px;
  text-align: center;
  flex-shrink: 0;
}

.home-icon {
  filter: saturate(0.8);
}

.nav-text {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.nav-count {
  font-size: 0.7rem;
  padding: 3px 8px;
  background: #e2e8f0;
  color: #64748b;
  border-radius: 10px;
  min-width: 20px;
  text-align: center;
  font-weight: 500;
  transition: all 0.2s;
}

.nav-count.highlight {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
}

.nav-item.active .nav-count {
  background: var(--category-color, #3b82f6);
  color: white;
}

.nav-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, #e2e8f0, transparent);
  margin: 20px 0;
}

.nav-section {
  padding: 0 4px;
}

.section-title {
  font-size: 0.75rem;
  font-weight: 600;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0 0 14px 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.section-icon {
  font-size: 0.9rem;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  font-size: 0.75rem;
  padding: 6px 12px;
  border-radius: 20px;
  border: 1px solid;
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 500;
}

.tag-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  filter: brightness(1.05);
}

.tags-empty {
  font-size: 0.8rem;
  color: #94a3b8;
  font-style: italic;
}
</style>
