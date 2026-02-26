<template>
  <AppLayout>
    <template #sidebar-left>
      <CategoryNav />
    </template>

    <template #main>
      <PromptEditor />
    </template>

    <template #sidebar-right>
      <HistoryPanel />
    </template>
  </AppLayout>
</template>

<script setup lang="ts">
import AppLayout from '@/components/layout/AppLayout.vue';
import CategoryNav from '@/components/layout/CategoryNav.vue';
import PromptEditor from '@/components/editor/PromptEditor.vue';
import HistoryPanel from '@/components/history/HistoryPanel.vue';
</script>
