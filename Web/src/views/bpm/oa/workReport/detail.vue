<template>
  <ContentWrap>
    <el-descriptions :column="1" border>
      <el-descriptions-item :label="t('oa.workReport.type')">
        <span v-if="detailData.type === 1">日报</span>
        <span v-else-if="detailData.type === 2">周报</span>
        <span v-else>月报</span>
      </el-descriptions-item>
      <el-descriptions-item :label="t('oa.workReport.reportTitle')">
        {{ detailData.title }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('oa.workReport.reportDate')">
        {{ detailData.reportDate }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('oa.workReport.content')">
        {{ detailData.content }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('oa.workReport.status')">
        <span v-if="detailData.status === 0">草稿</span>
        <span v-else-if="detailData.status === 1">已提交</span>
        <span v-else>已审批</span>
      </el-descriptions-item>
      <el-descriptions-item :label="t('oa.workReport.reviewComment')">
        {{ detailData.reviewComment || '-' }}
      </el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
import { propTypes } from '@/utils/propTypes'
import * as WorkReportApi from '@/api/bpm/workReport'

defineOptions({ name: 'BpmOAWorkReportDetail' })

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
    detailData.value = await WorkReportApi.getWorkReport(props.id || queryId)
  } finally {
    detailLoading.value = false
  }
}
defineExpose({ open: getInfo })

onMounted(() => {
  getInfo()
})
</script>