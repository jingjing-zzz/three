<template>
  <ContentWrap>
    <el-descriptions :column="1" border>
      <el-descriptions-item :label="t('oa.loan.amount')">
        {{ detailData.amount }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('oa.loan.reason')">
        {{ detailData.reason }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('oa.loan.repaymentDate')">
        {{ formatDate(detailData.repaymentDate, 'YYYY-MM-DD') }}
      </el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
import { formatDate } from '@/utils/formatTime'
import { propTypes } from '@/utils/propTypes'
import * as LoanApi from '@/api/bpm/loan'

defineOptions({ name: 'BpmOALoanDetail' })

const { t } = useI18n('bpm')
const { query } = useRoute()

const props = defineProps({
  id: propTypes.number.def(undefined)
})
const detailLoading = ref(false)
const detailData = ref<any>({})
const queryId = query.id as unknown as number

const getInfo = async () => {
  detailLoading.value = true
  try {
    detailData.value = await LoanApi.getLoan(props.id || queryId)
  } finally {
    detailLoading.value = false
  }
}
defineExpose({ open: getInfo })

onMounted(() => {
  getInfo()
})
</script>