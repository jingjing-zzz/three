<template>
  <ContentWrap :title="t('oa.businessTrip.title')">
    <el-descriptions v-loading="loading" :column="2" border>
      <el-descriptions-item :label="t('oa.businessTrip.destination')">{{ formData.destination }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.businessTrip.startTime')">{{ formatDateTime(formData.startTime) }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.businessTrip.endTime')">{{ formatDateTime(formData.endTime) }}</el-descriptions-item>
      <el-descriptions-item :label="t('oa.businessTrip.reason')">{{ formData.reason }}</el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
import { formatDate } from '@/utils/formatTime'
import * as BusinessTripApi from '@/api/bpm/businessTrip'

defineOptions({ name: 'BpmOABusinessTripDetail' })

const props = defineProps<{
  id?: string | number
}>()

const { t } = useI18n('bpm')

const formData = ref<any>({
  destination: undefined,
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
    formData.value = await BusinessTripApi.getBusinessTrip(Number(id))
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
