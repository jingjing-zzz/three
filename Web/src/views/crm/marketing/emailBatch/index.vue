<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('crm.marketing.emailBatch.campaignName')" prop="campaignName">
            <el-input
              v-model="queryParams.campaignName"
              class="!w-240px"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.marketing.emailBatch.status.label')" prop="status">
            <el-select
              v-model="queryParams.status"
              class="!w-240px"
              clearable
              :placeholder="t('common.selectText')"
            >
              <el-option :label="t('crm.marketing.emailBatch.status.pending')" :value="0" />
              <el-option :label="t('crm.marketing.emailBatch.status.sending')" :value="1" />
              <el-option :label="t('crm.marketing.emailBatch.status.completed')" :value="2" />
              <el-option :label="t('crm.marketing.emailBatch.status.failed')" :value="3" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery">
              <Icon class="mr-5px" icon="ep:search" />
              {{ t('common.search') }}
            </el-button>
            <el-button @click="resetQuery">
              <Icon class="mr-5px" icon="ep:refresh" />
              {{ t('common.reset') }}
            </el-button>
            <el-button type="primary" @click="openForm('create')">
              <Icon class="mr-5px" icon="ep:plus" />
              {{ t('common.add') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
      <el-table-column align="center" :label="t('crm.marketing.emailBatch.campaignName')" prop="campaignName" min-width="160" />
      <el-table-column align="center" :label="t('crm.marketing.emailBatch.templateName')" prop="templateName" min-width="120" />
      <el-table-column align="center" :label="t('crm.marketing.emailBatch.subject')" prop="subject" min-width="160" />
      <el-table-column align="center" :label="t('crm.marketing.emailBatch.content')" prop="content" min-width="200" />
      <el-table-column align="center" :label="t('crm.marketing.emailBatch.totalCount')" prop="totalCount" min-width="100" />
      <el-table-column align="center" :label="t('crm.marketing.emailBatch.sendCount')" prop="sendCount" min-width="100" />
      <el-table-column align="center" :label="t('crm.marketing.emailBatch.successCount')" prop="successCount" min-width="100" />
      <el-table-column align="center" :label="t('crm.marketing.emailBatch.failCount')" prop="failCount" min-width="100" />
      <el-table-column align="center" :label="t('crm.marketing.emailBatch.status.label')" prop="status" min-width="100">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">
            {{ getStatusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('common.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" fixed="right" :label="t('common.action')" width="250">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            {{ t('common.del') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <MarketingEmailBatchForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as MarketingEmailBatchApi from '@/api/crm/marketing/emailBatch'
import MarketingEmailBatchForm from './MarketingEmailBatchForm.vue'

defineOptions({ name: 'MarketingEmailBatch' })

const message = useMessage()
const { t } = useI18n()
const loading = ref(true)
const total = ref(0)
const list = ref([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  campaignName: null,
  status: null
})
const queryFormRef = ref()
const formRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await MarketingEmailBatchApi.getMarketingEmailBatchPage(queryParams)
    list.value = data.list
    total.value = data.total
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNo = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value.resetFields()
  handleQuery()
}

const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await MarketingEmailBatchApi.deleteMarketingEmailBatch(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const getStatusLabel = (status: number) => {
  const labels = {
    0: t('crm.marketing.emailBatch.status.pending'),
    1: t('crm.marketing.emailBatch.status.sending'),
    2: t('crm.marketing.emailBatch.status.completed'),
    3: t('crm.marketing.emailBatch.status.failed')
  }
  return labels[status] || status
}

const getStatusTagType = (status: number) => {
  const types = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return types[status] || 'info'
}

onMounted(() => {
  getList()
})
</script>