<template>
  <el-dialog
    v-model="visible"
    :title="title"
    width="600px"
    append-to-body
    destroy-on-close
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
      <el-form-item :label="t('crm.marketing.customerCare.name')" prop="name">
        <el-input v-model="form.name" :placeholder="t('common.pleaseInput')" />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.customerCare.type.label')" prop="type">
        <el-select v-model="form.type" :placeholder="t('common.selectText')">
          <el-option :label="t('crm.marketing.customerCare.type.birthday')" :value="1" />
          <el-option :label="t('crm.marketing.customerCare.type.anniversary')" :value="2" />
          <el-option :label="t('crm.marketing.customerCare.type.custom')" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('crm.marketing.customerCare.triggerType.label')" prop="triggerType">
        <el-select v-model="form.triggerType" :placeholder="t('common.selectText')">
          <el-option :label="t('crm.marketing.customerCare.triggerType.automatic')" :value="1" />
          <el-option :label="t('crm.marketing.customerCare.triggerType.manual')" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('crm.marketing.customerCare.triggerCondition')" prop="triggerCondition">
        <el-input v-model="form.triggerCondition" type="textarea" :rows="3" :placeholder="t('common.pleaseInput')" />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.customerCare.sendChannel.label')" prop="sendChannel">
        <el-select v-model="form.sendChannel" :placeholder="t('common.selectText')">
          <el-option :label="t('crm.marketing.customerCare.sendChannel.sms')" :value="1" />
          <el-option :label="t('crm.marketing.customerCare.sendChannel.email')" :value="2" />
          <el-option :label="t('crm.marketing.customerCare.sendChannel.wechat')" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('crm.marketing.customerCare.templateId')" prop="templateId">
        <el-input v-model="form.templateId" type="number" :placeholder="t('common.pleaseInput')" />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.customerCare.content')" prop="content">
        <el-input v-model="form.content" type="textarea" :rows="3" :placeholder="t('common.pleaseInput')" />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.customerCare.subject')" prop="subject">
        <el-input v-model="form.subject" :placeholder="t('common.pleaseInput')" />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.customerCare.status.label')" prop="status">
        <el-select v-model="form.status" :placeholder="t('common.selectText')">
          <el-option :label="t('crm.marketing.customerCare.status.enabled')" :value="1" />
          <el-option :label="t('crm.marketing.customerCare.status.disabled')" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('crm.marketing.customerCare.remark')" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="3" :placeholder="t('common.pleaseInput')" />
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
import * as MarketingCustomerCareApi from '@/api/crm/marketing/customerCare'

const emit = defineEmits(['success'])

const message = useMessage()
const { t } = useI18n()

const visible = ref(false)
const loading = ref(false)
const formRef = ref()
const title = ref('')

const form = reactive({
  id: null,
  name: null,
  type: null,
  triggerType: null,
  triggerCondition: null,
  sendChannel: null,
  templateId: null,
  content: null,
  subject: null,
  status: 1,
  remark: null
})

const rules = {
  name: [{ required: true, message: t('common.pleaseInput'), trigger: 'blur' }],
  type: [{ required: true, message: t('common.pleaseSelect'), trigger: 'change' }],
  triggerType: [{ required: true, message: t('common.pleaseSelect'), trigger: 'change' }],
  sendChannel: [{ required: true, message: t('common.pleaseSelect'), trigger: 'change' }],
  content: [{ required: true, message: t('common.pleaseInput'), trigger: 'blur' }]
}

const open = async (type: string, id?: number) => {
  visible.value = true
  title.value = type === 'create' ? t('common.add') : t('common.edit')
  resetForm()
  if (type === 'update' && id) {
    const data = await MarketingCustomerCareApi.getMarketingCustomerCare(id)
    Object.assign(form, data)
  }
}

const resetForm = () => {
  form.id = null
  form.name = null
  form.type = null
  form.triggerType = null
  form.triggerCondition = null
  form.sendChannel = null
  form.templateId = null
  form.content = null
  form.subject = null
  form.status = 1
  form.remark = null
}

const submitForm = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    if (form.id) {
      await MarketingCustomerCareApi.updateMarketingCustomerCare(form)
      message.success(t('common.updateSuccess'))
    } else {
      await MarketingCustomerCareApi.createMarketingCustomerCare(form)
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