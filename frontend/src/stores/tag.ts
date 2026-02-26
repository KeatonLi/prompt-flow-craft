import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { tagApi } from '@/api';
import type { Tag } from '@/types';

export const useTagStore = defineStore('tag', () => {
  // State
  const tags = ref<Tag[]>([]);
  const hotTags = ref<Tag[]>([]);
  const loading = ref(false);
  const selectedTagIds = ref<number[]>([]);

  // Getters
  const sortedTags = computed(() => {
    return [...tags.value].sort((a, b) => b.usageCount - a.usageCount);
  });

  const systemTags = computed(() => {
    return tags.value.filter(t => t.isSystem);
  });

  const selectedTags = computed(() => {
    return tags.value.filter(t => selectedTagIds.value.includes(t.id));
  });

  // Actions
  async function fetchTags() {
    loading.value = true;
    try {
      const data = await tagApi.getAll();
      tags.value = data;
    } finally {
      loading.value = false;
    }
  }

  async function fetchHotTags() {
    try {
      const data = await tagApi.getHot();
      hotTags.value = data;
    } catch (error) {
      console.error('Failed to fetch hot tags:', error);
    }
  }

  function toggleTagSelection(tagId: number) {
    const index = selectedTagIds.value.indexOf(tagId);
    if (index > -1) {
      selectedTagIds.value.splice(index, 1);
    } else {
      selectedTagIds.value.push(tagId);
    }
  }

  function clearTagSelection() {
    selectedTagIds.value = [];
  }

  function selectTags(tagIds: number[]) {
    selectedTagIds.value = [...tagIds];
  }

  function getTagById(id: number): Tag | undefined {
    return tags.value.find(t => t.id === id);
  }

  function getTagColor(id: number): string {
    const tag = getTagById(id);
    return tag?.color || '#3b82f6';
  }

  return {
    tags,
    hotTags,
    loading,
    selectedTagIds,
    sortedTags,
    systemTags,
    selectedTags,
    fetchTags,
    fetchHotTags,
    toggleTagSelection,
    clearTagSelection,
    selectTags,
    getTagById,
    getTagColor
  };
});
