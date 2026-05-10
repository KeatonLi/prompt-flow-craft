import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { promptRecordApi, type AgentRecord, type SkillRecord } from '@/api/promptRecord';

type RecordType = 'agent' | 'skill';

export const usePromptRecordStore = defineStore('promptRecord', () => {
  const agentRecords = ref<AgentRecord[]>([]);
  const skillRecords = ref<SkillRecord[]>([]);
  const currentType = ref<RecordType>('agent');
  const loading = ref(false);
  const pagination = ref({
    page: 1,
    size: 20,
    total: 0,
    totalPages: 0
  });
  const sortBy = ref<'createdAt' | 'likeCount'>('createdAt');

  const currentRecords = computed(() => {
    return currentType.value === 'agent' ? agentRecords.value : skillRecords.value;
  });

  async function fetchAgents() {
    loading.value = true;
    try {
      const data = await promptRecordApi.getAgentPage({
        page: pagination.value.page,
        size: pagination.value.size,
        sortBy: sortBy.value
      });
      agentRecords.value = data.list;
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

  async function fetchSkills() {
    loading.value = true;
    try {
      const data = await promptRecordApi.getSkillPage({
        page: pagination.value.page,
        size: pagination.value.size,
        sortBy: sortBy.value
      });
      skillRecords.value = data.list;
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

  async function fetchRecords() {
    if (currentType.value === 'agent') {
      await fetchAgents();
    } else {
      await fetchSkills();
    }
  }

  async function loadMore() {
    if (loading.value || pagination.value.page >= pagination.value.totalPages) return;
    loading.value = true;
    try {
      pagination.value.page++;
      let data;
      if (currentType.value === 'agent') {
        data = await promptRecordApi.getAgentPage({
          page: pagination.value.page,
          size: pagination.value.size,
          sortBy: sortBy.value
        });
        agentRecords.value = [...agentRecords.value, ...data.list];
      } else {
        data = await promptRecordApi.getSkillPage({
          page: pagination.value.page,
          size: pagination.value.size,
          sortBy: sortBy.value
        });
        skillRecords.value = [...skillRecords.value, ...data.list];
      }
      pagination.value.total = data.total;
      pagination.value.totalPages = data.totalPages;
    } finally {
      loading.value = false;
    }
  }

  async function deleteRecord(id: number) {
    try {
      if (currentType.value === 'agent') {
        await promptRecordApi.deleteAgent(id);
        agentRecords.value = agentRecords.value.filter(r => r.id !== id);
      } else {
        await promptRecordApi.deleteSkill(id);
        skillRecords.value = skillRecords.value.filter(r => r.id !== id);
      }
      pagination.value.total--;
    } catch (error) {
      console.error('Failed to delete record:', error);
    }
  }

  function setType(type: RecordType) {
    currentType.value = type;
    pagination.value.page = 1;
    fetchRecords();
  }

  function setSort(sort: 'createdAt' | 'likeCount') {
    sortBy.value = sort;
    pagination.value.page = 1;
    fetchRecords();
  }

  function getRecordById(id: number): AgentRecord | SkillRecord | undefined {
    if (currentType.value === 'agent') {
      return agentRecords.value.find(r => r.id === id);
    }
    return skillRecords.value.find(r => r.id === id);
  }

  return {
    agentRecords,
    skillRecords,
    currentType,
    loading,
    pagination,
    sortBy,
    currentRecords,
    fetchRecords,
    fetchAgents,
    fetchSkills,
    loadMore,
    deleteRecord,
    setType,
    setSort,
    getRecordById
  };
});