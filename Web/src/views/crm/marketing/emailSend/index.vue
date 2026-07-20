<template>
  <ContentWrap>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" class="email-send-form">
      <el-form-item :label="t('crm.marketing.emailSend.recipients')" prop="emails">
        <div class="recipients-wrapper">
          <el-select
            v-model="form.emails"
            multiple
            filterable
            allow-create
            default-first-option
            :reserve-keyword="false"
            :placeholder="t('crm.marketing.emailSend.recipientsPlaceholder')"
            class="!w-full"
            :no-data-text="'回车添加邮箱'"
          >
            <el-option
              v-for="email in form.emails"
              :key="email"
              :label="email"
              :value="email"
            />
          </el-select>
          <el-button type="primary" plain @click="openCustomerSelect">
            <Icon class="mr-5px" icon="ep:user" />
            {{ t('crm.marketing.emailSend.addFromCustomer') }}
          </el-button>
        </div>
      </el-form-item>

      <el-form-item :label="t('crm.marketing.emailSend.subject')" prop="subject">
        <el-input v-model="form.subject" :placeholder="t('common.inputText')" />
      </el-form-item>

      <el-form-item :label="t('crm.marketing.emailSend.content')" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="10"
          :placeholder="t('common.inputText')"
        />
      </el-form-item>

      <el-form-item :label="t('crm.marketing.emailSend.campaignName')">
        <el-input v-model="form.campaignName" placeholder="快速发送" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="sending" @click="handleSend">
          <Icon class="mr-5px" icon="ep:promotion" />
          {{ sending ? t('crm.marketing.emailSend.sending') : t('crm.marketing.emailSend.send') }}
        </el-button>
        <el-button @click="handleReset">{{ t('common.reset') }}</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 客户选择对话框 -->
  <el-dialog
    v-model="customerSelectVisible"
    :title="t('crm.marketing.emailSend.customerSelect.title')"
    width="900px"
    append-to-body
    destroy-on-close
  >
    <div class="customer-select-wrapper">
      <el-form :inline="true" class="search-form">
        <el-form-item :label="t('crm.marketing.emailSend.customerSelect.name')">
          <el-input
            v-model="customerQuery.name"
            :placeholder="t('crm.marketing.emailSend.customerSelect.searchPlaceholder')"
            clearable
            @keyup.enter="handleCustomerQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleCustomerQuery">
            <Icon class="mr-5px" icon="ep:search" />
            {{ t('common.search') }}
          </el-button>
          <el-button @click="resetCustomerQuery">
            <Icon class="mr-5px" icon="ep:refresh" />
            {{ t('common.reset') }}
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="customerQuery.onlyWithEmail">
            {{ t('crm.marketing.emailSend.customerSelect.onlyWithEmail') }}
          </el-checkbox>
        </el-form-item>
      </el-form>

      <el-alert
        v-if="selectedCustomers.length > 0"
        :title="t('crm.marketing.emailSend.customerSelect.selectedCount', { count: selectedCustomers.length })"
        type="info"
        :closable="false"
        class="mb-10px"
      />
      <el-alert
        :title="t('crm.marketing.emailSend.customerSelect.noEmailTip')"
        type="warning"
        :closable="false"
        class="mb-10px"
      />

      <el-table
        v-loading="customerLoading"
        :data="customerList"
        :show-overflow-tooltip="true"
        :stripe="true"
        height="400"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" :selectable="canSelect" />
        <el-table-column
          align="center"
          :label="t('crm.marketing.emailSend.customerSelect.name')"
          prop="name"
          min-width="160"
        />
        <el-table-column
          align="center"
          :label="t('crm.marketing.emailSend.customerSelect.email')"
          prop="email"
          min-width="200"
        >
          <template #default="scope">
            <span v-if="scope.row.email">{{ scope.row.email }}</span>
            <el-tag v-else type="info" size="small">无邮箱</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          :label="t('crm.marketing.emailSend.customerSelect.mobile')"
          prop="mobile"
          min-width="120"
        />
        <el-table-column
          align="center"
          :label="t('crm.marketing.emailSend.customerSelect.ownerUserName')"
          prop="ownerUserName"
          min-width="120"
        />
      </el-table>
      <Pagination
        v-model:limit="customerQuery.pageSize"
        v-model:page="customerQuery.pageNo"
        :total="customerTotal"
        @pagination="getCustomerList"
      />
    </div>
    <template #footer>
      <el-button @click="customerSelectVisible = false">{{ t('common.cancel') }}</el-button>
      <el-button type="primary" @click="confirmSelectCustomers">
        {{ t('crm.marketing.emailSend.customerSelect.confirm') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import * as EmailSendApi from '@/api/crm/marketing/emailSend'
import * as CustomerApi from '@/api/crm/customer'

defineOptions({ name: 'MarketingEmailSend' })

const message = useMessage()
const { t } = useI18n()
const formRef = ref()
const sending = ref(false)

const form = reactive({
  emails: [] as string[],
  subject: '',
  content: '',
  campaignName: ''
})

const validateEmails = (_rule: any, value: string[], callback: any) => {
  if (!value || value.length === 0) {
    callback(new Error(t('crm.marketing.emailSend.validation.emailsRequired')))
  } else {
    // 校验邮箱格式
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    const invalid = value.find((e) => !emailRegex.test(e))
    if (invalid) {
      callback(new Error(`邮箱格式不正确: ${invalid}`))
    } else {
      callback()
    }
  }
}

const rules = {
  emails: [{ required: true, validator: validateEmails, trigger: 'change' }],
  subject: [{ required: true, message: t('crm.marketing.emailSend.validation.subjectRequired'), trigger: 'blur' }],
  content: [{ required: true, message: t('crm.marketing.emailSend.validation.contentRequired'), trigger: 'blur' }]
}

// ============ 客户选择 ============
const customerSelectVisible = ref(false)
const customerLoading = ref(false)
const customerList = ref<any[]>([])
const customerTotal = ref(0)
const selectedCustomers = ref<any[]>([])
const customerQuery = reactive({
  pageNo: 1,
  pageSize: 10,
  name: '',
  onlyWithEmail: false
})

const openCustomerSelect = async () => {
  customerSelectVisible.value = true
  selectedCustomers.value = []
  await getCustomerList()
}

const getCustomerList = async () => {
  customerLoading.value = true
  try {
    const params: any = {
      pageNo: customerQuery.pageNo,
      pageSize: customerQuery.pageSize
    }
    if (customerQuery.name) {
      params.name = customerQuery.name
    }
    const data = await CustomerApi.getCustomerPage(params)
    customerList.value = customerQuery.onlyWithEmail
      ? (data.list || []).filter((c: any) => c.email)
      : data.list || []
    customerTotal.value = customerQuery.onlyWithEmail ? customerList.value.length : data.total
  } finally {
    customerLoading.value = false
  }
}

const handleCustomerQuery = () => {
  customerQuery.pageNo = 1
  getCustomerList()
}

const resetCustomerQuery = () => {
  customerQuery.name = ''
  customerQuery.onlyWithEmail = false
  customerQuery.pageNo = 1
  getCustomerList()
}

const canSelect = (row: any) => {
  // 没有邮箱的客户不能选中
  return !!row.email
}

const handleSelectionChange = (selection: any[]) => {
  selectedCustomers.value = selection
}

const confirmSelectCustomers = () => {
  if (selectedCustomers.value.length === 0) {
    message.warning('请至少选择一个客户')
    return
  }
  // 把选中客户的邮箱加入收件人列表（去重）
  const existing = new Set(form.emails)
  let added = 0
  selectedCustomers.value.forEach((c) => {
    if (c.email && !existing.has(c.email)) {
      form.emails.push(c.email)
      existing.add(c.email)
      added++
    }
  })
  if (added > 0) {
    message.success(`已添加 ${added} 个邮箱`)
  } else {
    message.info('没有新的邮箱被添加（可能已存在）')
  }
  customerSelectVisible.value = false
}

// ============ 发送 ============
const handleSend = async () => {
  await formRef.value.validate()
  try {
    await message.confirm(t('crm.marketing.emailSend.sendConfirm'))
  } catch {
    return
  }
  sending.value = true
  try {
    const result = await EmailSendApi.sendDirectEmail({
      emails: form.emails,
      subject: form.subject,
      content: form.content,
      campaignName: form.campaignName || undefined
    })
    const msg = t('crm.marketing.emailSend.sendResult', {
      total: result.totalCount,
      success: result.successCount,
      fail: result.failCount
    })
    if (result.failCount === 0) {
      message.success(msg)
    } else {
      message.warning(msg)
    }
    handleReset()
  } finally {
    sending.value = false
  }
}

const handleReset = () => {
  form.emails = []
  form.subject = ''
  form.content = ''
  form.campaignName = ''
  formRef.value?.clearValidate()
}
</script>

<style lang="scss" scoped>
.email-send-form {
  max-width: 900px;
}

.recipients-wrapper {
  display: flex;
  gap: 10px;
  width: 100%;
  align-items: flex-start;

  .el-select {
    flex: 1;
  }
}

.customer-select-wrapper {
  .search-form {
    margin-bottom: 10px;
  }
}
</style>
