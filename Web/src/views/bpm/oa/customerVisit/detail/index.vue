<template>
  <ContentWrap :title="t('oa.customerVisit.title')">
    <el-descriptions :column="2" border>
      <el-descriptions-item :label="t('oa.customerVisit.customerName')">{{ formData.customerName }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.customerVisit.customerContact')">{{ formData.customerContact }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.customerVisit.contactPhone')">{{ formData.contactPhone }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.customerVisit.visitTime')">{{ formatDateTime(formData.visitTime) }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.customerVisit.visitLocation')">{{ formData.visitLocation }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.customerVisit.purpose')" :span="2">{{ formData.purpose }}</el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
import { formatDate } from '@/utils/formatTime'

defineOptions({ name: 'BpmOACustomerVisitDetail' })

const props = defineProps<{
  id: string
}>()

const { t } = useI18n('bpm')

const formData = ref({
  customerName: undefined,
  customerContact: undefined,
  contactPhone: undefined,
  visitTime: undefined,
  visitLocation: undefined,
  purpose: undefined
})

const formatDateTime = (timestamp: string | undefined) => {
  if (!timestamp) return ''
  return formatDate(new Date(Number(timestamp)), 'yyyy-MM-dd HH:mm:ss')
}
</script>
