<template>
  <div class="category-nav">
    <div class="nav-header">
      <h3>分类</h3>
      <span class="count-badge">{{ categories.length }}</span>
    </div>

    <nav class="nav-list">
      <button
        class="nav-item"
        :class="{ active: currentCategoryId === null }"
        @click="selectCategory(null)"
      >
        <span class="nav-icon">🏠</span>
        <span class="nav-text">全部</span>
        <span class="nav-count">{{ totalCount }}</span>
      </button>

      <button
        v-for="category in categories"
        :key="category.id"
        class="nav-item"
        :class="{ active: currentCategoryId === category.id }"
        :style="{ '--category-color': category.color }"
        @click="selectCategory(category.id)"
      >
        <span class="nav-icon">{{ category.icon }}</span>
        <span class="nav-text">{{ category.name }}</span>
        <span class="nav-count">{{ getCategoryCount(category.id) }}</span>
      </button>
    </nav>

    <div class="nav-divider"></div>

    <div class="nav-section">
      <h4>快速筛选</h4>
      <button
        class="nav-item"
        :class="{ active: showFavoritesOnly }"
        @click="toggleFavorites"
      >
        <span class="nav-icon">⭐</span>
        <span class="nav-text">收藏</span>
        <span class="nav-count">{{ favoriteCount }}</span>
      </button>
    </div>

    <div class="nav-divider"></div>

    <div class="nav-section">
      <h4>热门标签</h4>
      <div class="tag-cloud">
        <span
          v-for="tag in hotTags"
          :key="tag.id"
          class="tag-item"
          :style="{ backgroundColor: tag.color + '20', color: tag.color, borderColor: tag.color }"
          @click="selectTag(tag.id)"
        >
          {{ tag.name }}
        </span>
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
const hotTags = computed(() => tagStore.hotTags.slice(0, 8));

// 使用 store 中的统计方法
const totalCount = computed(() => categoryStore.totalCount);
const favoriteCount = computed(() => categoryStore.favoriteCount);
const showFavoritesOnly = computed(() => historyStore.queryParams.isFavorite === true);

// 获取分类数量
function getCategoryCount(categoryId: number): number {
  return categoryStore.getCategoryCount(categoryId);
}

function selectCategory(id: number | null) {
  categoryStore.setCurrentCategory(id);
  historyStore.setQueryParams({
    categoryId: id ?? undefined,
    page: 1
  });
  historyStore.fetchRecords();
}

function toggleFavorites() {
  const newValue = showFavoritesOnly.value ? undefined : true;
  historyStore.setQueryParams({
    isFavorite: newValue,
    page: 1
  });
  historyStore.fetchRecords();
}

function selectTag(tagId: number) {
  tagStore.toggleTagSelection(tagId);
}

onMounted(() => {
  categoryStore.fetchCategories();
  categoryStore.fetchCategoryStats(); // 获取分类统计
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
}

.nav-header h3 {
  font-size: 0.875rem;
  font-weight: 600;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0;
}

.count-badge {
  font-size: 0.75rem;
  padding: 2px 8px;
  background: #e2e8f0;
  color: #64748b;
  border-radius: 12px;
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
  padding: 10px 12px;
  border: none;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
  font-size: 0.875rem;
  color: #475569;
  position: relative;
}

.nav-item:hover {
  background: #f1f5f9;
}

.nav-item.active {
  background: #eff6ff;
  color: #2563eb;
  font-weight: 500;
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  width: 3px;
  height: 24px;
  background: var(--category-color, #2563eb);
  border-radius: 0 3px 3px 0;
}

.nav-icon {
  font-size: 1.125rem;
  width: 24px;
  text-align: center;
}

.nav-text {
  flex: 1;
}

.nav-count {
  font-size: 0.75rem;
  padding: 2px 6px;
  background: #e2e8f0;
  color: #64748b;
  border-radius: 10px;
  min-width: 20px;
  text-align: center;
}

.nav-item.active .nav-count {
  background: #bfdbfe;
  color: #2563eb;
}

.nav-divider {
  height: 1px;
  background: #e2e8f0;
  margin: 16px 0;
}

.nav-section h4 {
  font-size: 0.75rem;
  font-weight: 600;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0 0 12px 0;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  font-size: 0.75rem;
  padding: 4px 10px;
  border-radius: 12px;
  border: 1px solid;
  cursor: pointer;
  transition: all 0.2s;
}

.tag-item:hover {
  opacity: 0.8;
  transform: translateY(-1px);
}
</style>
