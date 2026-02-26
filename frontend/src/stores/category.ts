import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { categoryApi } from '@/api';
import type { Category } from '@/types';

export const useCategoryStore = defineStore('category', () => {
  // State
  const categories = ref<Category[]>([]);
  const loading = ref(false);
  const currentCategoryId = ref<number | null>(null);

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
    fetchCategories,
    setCurrentCategory,
    getCategoryById,
    getCategoryColor
  };
});
