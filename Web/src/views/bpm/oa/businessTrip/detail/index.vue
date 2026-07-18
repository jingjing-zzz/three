<template>
  <ContentWrap :title="t('oa.businessTrip.title')">
    <el-descriptions :column="2" border>
      <el-descriptions-item :label="t('oa.businessTrip.destination')">{{ formData.destination }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.businessTrip.startTime')">{{ formatDateTime(formData.startTime) }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.businessTrip.endTime')">{{ formatDateTime(formData.endTime) }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.businessTrip.reason')">{{ formData.reason }}</el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
import { formatDate } from '@/utils/formatTime'

defineOptions({ name: 'BpmOABusinessTripDetail' })

const props = defineProps<{
  id: string
}>()

const { t } = useI18n('bpm')

const formData = ref({
  destination: undefined,
  startTime: undefined,
  endTime: undefined,
  reason: undefined
})

const formatDateTime = (timestamp: string | undefined) => {
  if (!timestamp) return ''
  return formatDate(new Date(Number(timestamp)), 'yyyy-MM-dd HH:mm:ss')
}
</script>
