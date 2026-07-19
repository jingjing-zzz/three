<template>
  <ContentWrap :title="t('oa.leave.title')">
    <el-descriptions v-loading="loading" :column="2" border>
      <el-descriptions-item :label="t('oa.leave.type')">{{ typeMap[formData.type] || formData.type }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.leave.startTime')">{{ formatDateTime(formData.startTime) }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.leave.endTime')">{{ formatDateTime(formData.endTime) }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.leave.reason')">{{ formData.reason }}</el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
import { formatDate } from '@/utils/formatTime'
import * as LeaveApi from '@/api/bpm/leave'

defineOptions({ name: 'BpmOALeaveDetail' })

const props = defineProps<{
  id?: string | number
}>()

const { t } = useI18n('bpm')

const formData = ref<any>({
  type: undefined,
  startTime: undefined,
  endTime: undefined,
  reason: undefined
})
const { query } = useRoute()
const loading = ref(false)

const getDetail = async () => {
  const id = props.id || query.id
  if (!id) return
  loading.value = true
  try {
    formData.value = await LeaveApi.getLeave(Number(id))
  } finally {
    loading.value = false
  }
}

onMounted(getDetail)
watch([() => props.id, () => query.id], getDetail)

const typeMap: Record<number | string, string> = {
  1: '病假',
  2: '事假',
  3: '婚假',
  4: '产假',
  5: '陪产假',
  6: '丧假',
  7: '年假',
  8: '调休',
  9: '探亲假',
  10: '工伤假',
  11: '其他'
}

const formatDateTime = (timestamp: string | undefined) => {
  if (!timestamp) return ''
  return formatDate(new Date(Number(timestamp)), 'yyyy-MM-dd HH:mm:ss')
}
</script>
