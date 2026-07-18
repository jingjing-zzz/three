<template>
  <ContentWrap>
    <el-descriptions :column="1" border>
      <el-descriptions-item :label="t('oa.businessTrip.destination')">
        {{ detailData.destination }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('oa.businessTrip.startTime')">
        {{ formatDate(detailData.startTime, 'YYYY-MM-DD') }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('oa.businessTrip.endTime')">
        {{ formatDate(detailData.endTime, 'YYYY-MM-DD') }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('oa.businessTrip.day')">
        {{ detailData.day }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('oa.businessTrip.reason')">
        {{ detailData.reason }}
      </el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
import { formatDate } from '@/utils/formatTime'
import { propTypes } from '@/utils/propTypes'
import * as BusinessTripApi from '@/api/bpm/businessTrip'

defineOptions({ name: 'BpmOABusinessTripDetail' })

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
    detailData.value = await BusinessTripApi.getBusinessTrip(props.id || queryId)
  } finally {
    detailLoading.value = false
  }
}
defineExpose({ open: getInfo })

onMounted(() => {
  getInfo()
})
</script>