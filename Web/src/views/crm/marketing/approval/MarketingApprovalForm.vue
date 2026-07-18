<template>
  <el-dialog
    v-model="visible"
    :title="title"
    width="600px"
    append-to-body
    destroy-on-close
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
      <el-form-item :label="t('crm.marketing.approval.campaignId')" prop="campaignId">
        <el-input v-model="form.campaignId" type="number" />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.approval.campaignName')" prop="campaignName">
        <el-input v-model="form.campaignName" />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.approval.type.label')" prop="type">
        <el-select v-model="form.type" :placeholder="t('common.selectText')">
          <el-option :label="t('crm.marketing.approval.type.sms')" :value="1" />
          <el-option :label="t('crm.marketing.approval.type.email')" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('crm.marketing.approval.targetCount')" prop="targetCount">
        <el-input v-model="form.targetCount" type="number" :placeholder="t('common.pleaseInput')" />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.approval.contentPreview')" prop="contentPreview">
        <el-input v-model="form.contentPreview" type="textarea" :rows="3" :placeholder="t('common.pleaseInput')" />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.approval.status.label')" prop="status">
        <el-select v-model="form.status" :placeholder="t('common.selectText')">
          <el-option :label="t('crm.marketing.approval.status.pending')" :value="0" />
          <el-option :label="t('crm.marketing.approval.status.approved')" :value="1" />
          <el-option :label="t('crm.marketing.approval.status.rejected')" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('crm.marketing.approval.approveRemark')" prop="approveRemark">
        <el-input v-model="form.approveRemark" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">
        {{ t('common.cancel') }}
      </el-button>
      <el-button type="primary" @click="submitForm" :loading="loading">
        {{ t('common.confirm') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import * as MarketingApprovalApi from '@/api/crm/marketing/approval'

const emit = defineEmits(['success'])

const message = useMessage()
const { t } = useI18n()

const visible = ref(false)
const loading = ref(false)
const formRef = ref()
const title = ref('')

const form = reactive({
  id: null,
  campaignId: null,
  campaignName: null,
  type: null,
  targetCount: null,
  contentPreview: null,
  status: 0,
  approver: null,
  approveRemark: null
})

const rules = {
  campaignId: [{ required: true, message: t('common.pleaseInput'), trigger: 'blur' }],
  type: [{ required: true, message: t('common.pleaseSelect'), trigger: 'change' }],
  targetCount: [{ required: true, message: t('common.pleaseInput'), trigger: 'blur' }]
}

const open = async (type: string, id?: number) => {
  visible.value = true
  title.value = type === 'create' ? t('common.add') : t('common.edit')
  resetForm()
  if (type === 'update' && id) {
    const data = await MarketingApprovalApi.getMarketingApproval(id)
    Object.assign(form, data)
  }
}

const resetForm = () => {
  form.id = null
  form.campaignId = null
  form.campaignName = null
  form.type = null
  form.targetCount = null
  form.contentPreview = null
  form.status = 0
  form.approver = null
  form.approveRemark = null
}

const submitForm = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    if (form.id) {
      await MarketingApprovalApi.updateMarketingApproval(form)
      message.success(t('common.updateSuccess'))
    } else {
      await MarketingApprovalApi.createMarketingApproval(form)
      message.success(t('common.createSuccess'))
    }
    visible.value = false
    emit('success')
  } finally {
    loading.value = false
  }
}

defineExpose({
  open
})
</script>