<template>
  <ContentWrap :title="t('oa.customerVisit.title')">
    <el-descriptions v-loading="loading" :column="2" border>
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
import * as CustomerVisitApi from '@/api/bpm/customerVisit'

defineOptions({ name: 'BpmOACustomerVisitDetail' })

const props = defineProps<{
  id?: string | number
}>()

const { t } = useI18n('bpm')

const formData = ref<any>({
  customerName: undefined,
  customerContact: undefined,
  contactPhone: undefined,
  visitTime: undefined,
  visitLocation: undefined,
  purpose: undefined
})
const { query } = useRoute()
const loading = ref(false)

const getDetail = async () => {
  const id = props.id || query.id
  if (!id) return
  loading.value = true
  try {
    formData.value = await CustomerVisitApi.getCustomerVisit(Number(id))
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
