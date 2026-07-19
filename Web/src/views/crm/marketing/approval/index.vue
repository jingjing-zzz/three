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
          <el-form-item :label="t('crm.marketing.approval.campaignName')" prop="campaignName">
            <el-input
              v-model="queryParams.campaignName"
              class="!w-240px"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.marketing.approval.type.label')" prop="type">
            <el-select
              v-model="queryParams.type"
              class="!w-240px"
              clearable
              :placeholder="t('common.selectText')"
            >
              <el-option :label="t('crm.marketing.approval.type.sms')" :value="1" />
              <el-option :label="t('crm.marketing.approval.type.email')" :value="2" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.marketing.approval.status.label')" prop="status">
            <el-select
              v-model="queryParams.status"
              class="!w-240px"
              clearable
              :placeholder="t('common.selectText')"
            >
              <el-option :label="t('crm.marketing.approval.status.pending')" :value="0" />
              <el-option :label="t('crm.marketing.approval.status.approved')" :value="1" />
              <el-option :label="t('crm.marketing.approval.status.rejected')" :value="2" />
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
      <el-table-column align="center" :label="t('crm.marketing.approval.campaignName')" prop="campaignName" min-width="160" />
      <el-table-column align="center" :label="t('crm.marketing.approval.type.label')" prop="type" min-width="120">
        <template #default="scope">
          {{ scope.row.type === 1 ? t('crm.marketing.approval.type.sms') : t('crm.marketing.approval.type.email') }}
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('crm.marketing.approval.applicant')" prop="applicant" min-width="120" />
      <el-table-column align="center" :label="t('crm.marketing.approval.targetCount')" prop="targetCount" min-width="100" />
      <el-table-column align="center" :label="t('crm.marketing.approval.contentPreview')" prop="contentPreview" min-width="200" />
      <el-table-column align="center" :label="t('crm.marketing.approval.status.label')" prop="status" min-width="120">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">
            {{ getStatusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('crm.marketing.approval.approver')" prop="approver" min-width="120" />
      <el-table-column align="center" :label="t('crm.marketing.approval.approveRemark')" prop="approveRemark" min-width="150" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('crm.marketing.approval.applyTime')"
        prop="applyTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('crm.marketing.approval.approveTime')"
        prop="approveTime"
        min-width="180"
      />
      <el-table-column align="center" fixed="right" :label="t('common.action')" width="400">
        <template #default="scope">
          <el-button
            v-if="scope.row.status === 0"
            link
            type="success"
            @click="handleApprove(scope.row.id)"
          >
            {{ t('crm.marketing.approval.approve') }}
          </el-button>
          <el-button
            v-if="scope.row.status === 0"
            link
            type="danger"
            @click="handleReject(scope.row.id)"
          >
            {{ t('crm.marketing.approval.reject') }}
          </el-button>
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

  <MarketingApprovalForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as MarketingApprovalApi from '@/api/crm/marketing/approval'
import MarketingApprovalForm from './MarketingApprovalForm.vue'

defineOptions({ name: 'MarketingApproval' })

const message = useMessage()
const { t } = useI18n()
const loading = ref(true)
const total = ref(0)
const list = ref([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  campaignName: null,
  type: null,
  status: null
})
const queryFormRef = ref()
const formRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await MarketingApprovalApi.getMarketingApprovalPage(queryParams)
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

const handleApprove = async (id: number) => {
  try {
    await message.confirm(t('crm.marketing.approval.approveConfirm'))
    await MarketingApprovalApi.approveMarketingApproval(id)
    message.success(t('crm.marketing.approval.approveSuccess'))
    await getList()
  } catch {}
}

const handleReject = async (id: number) => {
  try {
    await message.confirm(t('crm.marketing.approval.rejectConfirm'))
    await MarketingApprovalApi.rejectMarketingApproval(id)
    message.success(t('crm.marketing.approval.rejectSuccess'))
    await getList()
  } catch {}
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await MarketingApprovalApi.deleteMarketingApproval(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const getStatusLabel = (status: number) => {
  const labels = {
    0: t('crm.marketing.approval.status.pending'),
    1: t('crm.marketing.approval.status.approved'),
    2: t('crm.marketing.approval.status.rejected')
  }
  return labels[status] || status
}

const getStatusTagType = (status: number) => {
  const types = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return types[status] || 'info'
}

onMounted(() => {
  getList()
})
</script>