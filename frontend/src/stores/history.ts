import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { historyApi } from '@/api';
import type { PromptRecord, PagedResult, HistoryQueryRequest } from '@/types';

export const useHistoryStore = defineStore('history', () => {
  const records = ref<PromptRecord[]>([]);
  const loading = ref(false);
  const pagination = ref({
    page: 1,
    size: 20,
    total: 0,
    totalPages: 0
  });
  const queryParams = ref<HistoryQueryRequest>({
    page: 1,
    size: 20,
    categoryId: undefined,
    keyword: '',
    sortBy: 'createdAt'
  });

  const filteredRecords = computed(() => {
    let result = [...records.value];

    if (queryParams.value.categoryId) {
      result = result.filter(r => r.categoryId === queryParams.value.categoryId);
    }

    if (queryParams.value.keyword) {
      const keyword = queryParams.value.keyword.toLowerCase();
      result = result.filter(r =>
        r.taskDescription.toLowerCase().includes(keyword) ||
        r.generatedPrompt.toLowerCase().includes(keyword)
      );
    }

    return result;
  });

  async function fetchRecords(params?: Partial<HistoryQueryRequest>) {
    loading.value = true;
    try {
      const mergedParams = { ...queryParams.value, ...params };
      const data: PagedResult<PromptRecord> = await historyApi.getPage(mergedParams);
      records.value = data.list;
      pagination.value = {
        page: data.page,
        size: data.size,
        total: data.total,
        totalPages: data.totalPages
      };
    } finally {
      loading.value = false;
    }
  }

  async function deleteRecord(id: number) {
    try {
      await historyApi.delete(id);
      records.value = records.value.filter(r => r.id !== id);
      pagination.value.total--;
    } catch (error) {
      console.error('Failed to delete record:', error);
    }
  }

  async function batchDelete(ids: number[]) {
    try {
      await historyApi.batchDelete(ids);
      records.value = records.value.filter(r => !ids.includes(r.id));
      pagination.value.total -= ids.length;
    } catch (error) {
      console.error('Failed to batch delete:', error);
    }
  }

  function setQueryParams(params: Partial<HistoryQueryRequest>) {
    queryParams.value = { ...queryParams.value, ...params };
  }

  function resetQueryParams() {
    queryParams.value = {
      page: 1,
      size: 20,
      categoryId: undefined,
      keyword: '',
      sortBy: 'createdAt'
    };
  }

  function getRecordById(id: number): PromptRecord | undefined {
    return records.value.find(r => r.id === id);
  }

  return {
    records,
    loading,
    pagination,
    queryParams,
    filteredRecords,
    fetchRecords,
    deleteRecord,
    batchDelete,
    setQueryParams,
    resetQueryParams,
    getRecordById
  };
});
