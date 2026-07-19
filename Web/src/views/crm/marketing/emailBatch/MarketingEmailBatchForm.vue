<template>
  <el-dialog
    v-model="visible"
    :title="title"
    width="600px"
    append-to-body
    destroy-on-close
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
      <el-form-item :label="t('crm.marketing.emailBatch.campaignId')" prop="campaignId">
        <el-input v-model="form.campaignId" type="number" />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.emailBatch.campaignName')" prop="campaignName">
        <el-input v-model="form.campaignName" />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.emailBatch.templateId')" prop="templateId">
        <el-input v-model="form.templateId" type="number" />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.emailBatch.templateName')" prop="templateName">
        <el-input v-model="form.templateName" />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.emailBatch.subject')" prop="subject">
        <el-input v-model="form.subject" />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.emailBatch.content')" prop="content">
        <el-input v-model="form.content" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.emailBatch.status')" prop="status">
        <el-select v-model="form.status" :placeholder="t('common.selectText')">
          <el-option :label="t('crm.marketing.emailBatch.status.pending')" :value="0" />
          <el-option :label="t('crm.marketing.emailBatch.status.sending')" :value="1" />
          <el-option :label="t('crm.marketing.emailBatch.status.completed')" :value="2" />
          <el-option :label="t('crm.marketing.emailBatch.status.failed')" :value="3" />
        </el-select>
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
import * as MarketingEmailBatchApi from '@/api/crm/marketing/emailBatch'

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
  templateId: null,
  templateName: null,
  subject: null,
  content: null,
  status: 0
})

const rules = {
  campaignId: [{ required: true, message: t('common.pleaseInput'), trigger: 'blur' }],
  subject: [{ required: true, message: t('common.pleaseInput'), trigger: 'blur' }],
  content: [{ required: true, message: t('common.pleaseInput'), trigger: 'blur' }]
}

const open = async (type: string, id?: number) => {
  visible.value = true
  title.value = type === 'create' ? t('common.add') : t('common.edit')
  resetForm()
  if (type === 'update' && id) {
    const data = await MarketingEmailBatchApi.getMarketingEmailBatch(id)
    Object.assign(form, data)
  }
}

const resetForm = () => {
  form.id = null
  form.campaignId = null
  form.campaignName = null
  form.templateId = null
  form.templateName = null
  form.subject = null
  form.content = null
  form.status = 0
}

const submitForm = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    if (form.id) {
      await MarketingEmailBatchApi.updateMarketingEmailBatch(form)
      message.success(t('common.updateSuccess'))
    } else {
      await MarketingEmailBatchApi.createMarketingEmailBatch(form)
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