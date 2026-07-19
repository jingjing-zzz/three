<template>
  <el-dialog
    v-model="visible"
    :title="title"
    width="600px"
    append-to-body
    destroy-on-close
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
      <el-form-item :label="t('crm.marketing.campaign.name')" prop="name">
        <el-input v-model="form.name" :placeholder="t('crm.marketing.campaign.namePlaceholder')" />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.campaign.type.label')" prop="type">
        <el-select v-model="form.type" :placeholder="t('common.selectText')">
          <el-option :label="t('crm.marketing.campaign.type.sms')" :value="1" />
          <el-option :label="t('crm.marketing.campaign.type.email')" :value="2" />
          <el-option :label="t('crm.marketing.campaign.type.wechat')" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('crm.marketing.campaign.status.label')" prop="status">
        <el-select v-model="form.status" :placeholder="t('common.selectText')">
          <el-option :label="t('crm.marketing.campaign.status.draft')" :value="0" />
          <el-option :label="t('crm.marketing.campaign.status.active')" :value="1" />
          <el-option :label="t('crm.marketing.campaign.status.completed')" :value="2" />
          <el-option :label="t('crm.marketing.campaign.status.paused')" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item :label="t('crm.marketing.campaign.startTime')" prop="startTime">
        <el-date-picker
          v-model="form.startTime"
          type="datetime"
          :placeholder="t('common.selectText')"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.campaign.endTime')" prop="endTime">
        <el-date-picker
          v-model="form.endTime"
          type="datetime"
          :placeholder="t('common.selectText')"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.campaign.targetFilter')" prop="targetFilter">
        <el-input v-model="form.targetFilter" type="textarea" :rows="3" />
      </el-form-item>
      <el-form-item :label="t('crm.marketing.campaign.description')" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="3" />
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
import * as MarketingCampaignApi from '@/api/crm/marketing/campaign'

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
  status: 0,
  startTime: null,
  endTime: null,
  targetFilter: null,
  description: null
})

const rules = {
  name: [{ required: true, message: t('crm.marketing.campaign.namePlaceholder'), trigger: 'blur' }],
  type: [{ required: true, message: t('common.pleaseSelect'), trigger: 'change' }],
  status: [{ required: true, message: t('common.pleaseSelect'), trigger: 'change' }]
}

const open = async (type: string, id?: number) => {
  visible.value = true
  title.value = type === 'create' ? t('common.add') : t('common.edit')
  resetForm()
  if (type === 'update' && id) {
    const data = await MarketingCampaignApi.getMarketingCampaign(id)
    Object.assign(form, data)
  }
}

const resetForm = () => {
  form.id = null
  form.name = null
  form.type = null
  form.status = 0
  form.startTime = null
  form.endTime = null
  form.targetFilter = null
  form.description = null
}

const submitForm = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    if (form.id) {
      await MarketingCampaignApi.updateMarketingCampaign(form)
      message.success(t('common.updateSuccess'))
    } else {
      await MarketingCampaignApi.createMarketingCampaign(form)
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