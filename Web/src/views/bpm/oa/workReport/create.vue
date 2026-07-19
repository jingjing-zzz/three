<template>
  <ContentWrap :title="t('oa.workReport.title') + t('common.info')">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="80px"
    >
      <el-form-item :label="t('oa.workReport.type')" prop="type">
        <el-select v-model="formData.type" :placeholder="t('oa.workReport.typePlaceholder')">
          <el-option label="日报" :value="1" />
          <el-option label="周报" :value="2" />
          <el-option label="月报" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('oa.workReport.reportTitle')" prop="title">
        <el-input v-model="formData.title" :placeholder="t('oa.workReport.reportTitlePlaceholder')" />
      </el-form-item>
      <el-form-item :label="t('oa.workReport.reportDate')" prop="reportDate">
        <el-date-picker
          v-model="formData.reportDate"
          clearable
          :placeholder="t('common.selectDate')"
          type="date"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <el-form-item :label="t('oa.workReport.content')" prop="content">
        <el-input v-model="formData.content" :placeholder="t('oa.workReport.contentPlaceholder')" type="textarea" :rows="10" />
      </el-form-item>
      <el-form-item>
        <el-button :disabled="formLoading" type="primary" @click="submitForm">
          {{ t('common.ok') }}
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>
</template>
<script lang="ts" setup>
import * as WorkReportApi from '@/api/bpm/workReport'
import { useTagsViewStore } from '@/store/modules/tagsView'

defineOptions({ name: 'BpmOAWorkReportCreate' })

const message = useMessage()
const { t } = useI18n('bpm')
const { delView } = useTagsViewStore()
const { push, currentRoute } = useRouter()

const formLoading = ref(false)
const formData = ref({
  type: undefined,
  title: undefined,
  content: undefined,
  reportDate: undefined
})
const formRules = reactive({
  type: [{ required: true, message: t('oa.workReport.type') + t('common.notEmpty'), trigger: 'blur' }],
  title: [{ required: true, message: t('oa.workReport.reportTitle') + t('common.notEmpty'), trigger: 'blur' }],
  content: [{ required: true, message: t('oa.workReport.content') + t('common.notEmpty'), trigger: 'change' }],
  reportDate: [{ required: true, message: t('oa.workReport.reportDate') + t('common.notEmpty'), trigger: 'change' }]
})
const formRef = ref()

const submitForm = async () => {
  if (!formRef) return
  const valid = await formRef.value.validate()
  if (!valid) return

  formLoading.value = true
  try {
    const data = { ...formData.value } as unknown as WorkReportApi.WorkReportVO
    await WorkReportApi.createWorkReport(data)
    message.success(t('common.createSuccess'))
    delView(unref(currentRoute))
    await push({ name: 'BpmOAWorkReport' })
  } finally {
    formLoading.value = false
  }
}
</script>