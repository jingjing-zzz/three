<template>
  <ContentWrap>
    <el-descriptions :column="1" border>
      <el-descriptions-item :label="t('oa.customerVisit.customerName')">
        {{ detailData.customerName }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('oa.customerVisit.customerContact')">
        {{ detailData.customerContact }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('oa.customerVisit.contactPhone')">
        {{ detailData.contactPhone }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('oa.customerVisit.visitTime')">
        {{ formatDate(detailData.visitTime, 'YYYY-MM-DD') }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('oa.customerVisit.visitLocation')">
        {{ detailData.visitLocation }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('oa.customerVisit.purpose')">
        {{ detailData.purpose }}
      </el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
import { formatDate } from '@/utils/formatTime'
import { propTypes } from '@/utils/propTypes'
import * as CustomerVisitApi from '@/api/bpm/customerVisit'

defineOptions({ name: 'BpmOACustomerVisitDetail' })

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
    detailData.value = await CustomerVisitApi.getCustomerVisit(props.id || queryId)
  } finally {
    detailLoading.value = false
  }
}
defineExpose({ open: getInfo })

onMounted(() => {
  getInfo()
})
</script>