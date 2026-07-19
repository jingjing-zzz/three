<template>
  <ContentWrap :title="t('oa.loan.title')">
    <el-descriptions v-loading="loading" :column="2" border>
      <el-descriptions-item :label="t('oa.loan.amount')">{{ formData.amount }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.loan.repaymentDate')">{{ formatDateTime(formData.repaymentDate) }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.loan.reason')" :span="2">{{ formData.reason }}</el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
import { formatDate } from '@/utils/formatTime'
import * as LoanApi from '@/api/bpm/loan'

defineOptions({ name: 'BpmOALoanDetail' })

const props = defineProps<{
  id?: string | number
}>()

const { t } = useI18n('bpm')

const formData = ref<any>({
  amount: undefined,
  reason: undefined,
  repaymentDate: undefined
})
const { query } = useRoute()
const loading = ref(false)

const getDetail = async () => {
  const id = props.id || query.id
  if (!id) return
  loading.value = true
  try {
    formData.value = await LoanApi.getLoan(Number(id))
  } finally {
    loading.value = false
  }
}

onMounted(getDetail)
watch([() => props.id, () => query.id], getDetail)

const formatDateTime = (timestamp: string | undefined) => {
  if (!timestamp) return ''
  return formatDate(new Date(Number(timestamp)), 'yyyy-MM-dd HH:mm:ss')
}
</script>
