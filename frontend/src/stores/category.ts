import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { categoryApi } from '@/api';
import type { Category } from '@/types';

export const useCategoryStore = defineStore('category', () => {
  // State
  const categories = ref<Category[]>([]);
  const loading = ref(false);
  const currentCategoryId = ref<number | null>(null);
  
  // 分类统计
  const categoryStats = ref({
    categoryCounts: {} as Record<number, number>,
    totalCount: 0,
    favoriteCount: 0
  });

  // Getters
  const sortedCategories = computed(() => {
    return [...categories.value].sort((a, b) => a.sortOrder - b.sortOrder);
  });

  const systemCategories = computed(() => {
    return categories.value.filter(c => c.isSystem);
  });

  const currentCategory = computed(() => {
    return categories.value.find(c => c.id === currentCategoryId.value) || null;
  });
  
  // 获取分类数量
  const getCategoryCount = (id: number): number => {
    return categoryStats.value.categoryCounts[id] || 0;
  };
  
  // 获取总数量
  const totalCount = computed(() => categoryStats.value.totalCount);
  
  // 获取收藏数量
  const favoriteCount = computed(() => categoryStats.value.favoriteCount);

  // Actions
  async function fetchCategories() {
    loading.value = true;
    try {
      const data = await categoryApi.getAll();
      categories.value = data;
    } finally {
      loading.value = false;
    }
  }
  
  // 获取分类统计
  async function fetchCategoryStats() {
    try {
      const data = await categoryApi.getStats();
      categoryStats.value = data;
    } catch (error) {
      console.error('获取分类统计失败:', error);
    }
  }

  function setCurrentCategory(id: number | null) {
    currentCategoryId.value = id;
  }

  function getCategoryById(id: number): Category | undefined {
    return categories.value.find(c => c.id === id);
  }

  function getCategoryColor(id: number): string {
    const category = getCategoryById(id);
    return category?.color || '#6b7280';
  }

  return {
    categories,
    loading,
    currentCategoryId,
    sortedCategories,
    systemCategories,
    currentCategory,
    categoryStats,
    totalCount,
    favoriteCount,
    getCategoryCount,
    fetchCategories,
    fetchCategoryStats,
    setCurrentCategory,
    getCategoryById,
    getCategoryColor
  };
});
