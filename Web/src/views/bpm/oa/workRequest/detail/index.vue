<template>
  <ContentWrap :title="t('oa.workRequest.title')">
    <el-descriptions :column="2" border>
      <el-descriptions-item :label="t('oa.workRequest.requestTitle')">{{ formData.title }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.workRequest.content')" :span="2">{{ formData.content }}</el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
defineOptions({ name: 'BpmOAWorkRequestDetail' })

const props = defineProps<{
  id: string
}>()

const { t } = useI18n('bpm')

const formData = ref({
  title: undefined,
  content: undefined
})
</script>
