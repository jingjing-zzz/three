<template>
  <ContentWrap :title="t('oa.loan.title')">
    <el-descriptions :column="2" border>
      <el-descriptions-item :label="t('oa.loan.amount')">{{ formData.amount }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.loan.repaymentDate')">{{ formatDateTime(formData.repaymentDate) }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.loan.reason')" :span="2">{{ formData.reason }}</el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
import { formatDate } from '@/utils/formatTime'

defineOptions({ name: 'BpmOALoanDetail' })

const props = defineProps<{
  id: string
}>()

const { t } = useI18n('bpm')

const formData = ref({
  amount: undefined,
  reason: undefined,
  repaymentDate: undefined
})

const formatDateTime = (timestamp: string | undefined) => {
  if (!timestamp) return ''
  return formatDate(new Date(Number(timestamp)), 'yyyy-MM-dd HH:mm:ss')
}
</script>
