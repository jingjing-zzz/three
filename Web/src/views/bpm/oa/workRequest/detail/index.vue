<template>
  <ContentWrap :title="t('oa.workRequest.title')">
    <el-descriptions v-loading="loading" :column="2" border>
      <el-descriptions-item :label="t('oa.workRequest.requestTitle')">{{ formData.title }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.workRequest.content')" :span="2">{{ formData.content }}</el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
import * as WorkRequestApi from '@/api/bpm/workRequest'

defineOptions({ name: 'BpmOAWorkRequestDetail' })

const props = defineProps<{
  id?: string | number
}>()

const { t } = useI18n('bpm')

const formData = ref<any>({
  title: undefined,
  content: undefined
})
const { query } = useRoute()
const loading = ref(false)

const getDetail = async () => {
  const id = props.id || query.id
  if (!id) return
  loading.value = true
  try {
    formData.value = await WorkRequestApi.getWorkRequest(Number(id))
  } finally {
    loading.value = false
  }
}

onMounted(getDetail)
watch([() => props.id, () => query.id], getDetail)
</script>
