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
          <el-form-item :label="t('crm.marketing.campaign.name')" prop="name">
            <el-input
              v-model="queryParams.name"
              class="!w-240px"
              clearable
              :placeholder="t('crm.marketing.campaign.namePlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.marketing.campaign.type.label')" prop="type">
            <el-select
              v-model="queryParams.type"
              class="!w-240px"
              clearable
              :placeholder="t('common.selectText')"
            >
              <el-option :label="t('crm.marketing.campaign.type.sms')" :value="1" />
              <el-option :label="t('crm.marketing.campaign.type.email')" :value="2" />
              <el-option :label="t('crm.marketing.campaign.type.wechat')" :value="3" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.marketing.campaign.status.label')" prop="status">
            <el-select
              v-model="queryParams.status"
              class="!w-240px"
              clearable
              :placeholder="t('common.selectText')"
            >
              <el-option :label="t('crm.marketing.campaign.status.draft')" :value="0" />
              <el-option :label="t('crm.marketing.campaign.status.active')" :value="1" />
              <el-option :label="t('crm.marketing.campaign.status.completed')" :value="2" />
              <el-option :label="t('crm.marketing.campaign.status.paused')" :value="3" />
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
      <el-table-column align="center" fixed="left" :label="t('crm.marketing.campaign.name')" prop="name" min-width="160">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('crm.marketing.campaign.type.label')" prop="type" min-width="100">
        <template #default="scope">
          <el-tag :type="getTypeTagType(scope.row.type)">
            {{ getTypeLabel(scope.row.type) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('crm.marketing.campaign.status.label')" prop="status" min-width="100">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">
            {{ getStatusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('crm.marketing.campaign.startTime')"
        prop="startTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('crm.marketing.campaign.endTime')"
        prop="endTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('crm.marketing.campaign.totalTargetCount')" prop="totalTargetCount" min-width="120" />
      <el-table-column align="center" :label="t('crm.marketing.campaign.sendCount')" prop="sendCount" min-width="100" />
      <el-table-column align="center" :label="t('crm.marketing.campaign.successCount')" prop="successCount" min-width="100" />
      <el-table-column align="center" :label="t('crm.marketing.campaign.failCount')" prop="failCount" min-width="100" />
      <el-table-column align="center" :label="t('crm.marketing.campaign.description')" prop="description" min-width="200" />
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

  <MarketingCampaignForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as MarketingCampaignApi from '@/api/crm/marketing/campaign'
import MarketingCampaignForm from './MarketingCampaignForm.vue'

defineOptions({ name: 'MarketingCampaign' })

const message = useMessage()
const { t } = useI18n()
const loading = ref(true)
const total = ref(0)
const list = ref([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  name: null,
  type: null,
  status: null
})
const queryFormRef = ref()
const formRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await MarketingCampaignApi.getMarketingCampaignPage(queryParams)
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

const openDetail = (id: number) => {
  openForm('update', id)
}

const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await MarketingCampaignApi.deleteMarketingCampaign(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const getTypeLabel = (type: number) => {
  const labels = {
    1: t('crm.marketing.campaign.type.sms'),
    2: t('crm.marketing.campaign.type.email'),
    3: t('crm.marketing.campaign.type.wechat')
  }
  return labels[type] || type
}

const getTypeTagType = (type: number) => {
  const types = {
    1: 'warning',
    2: 'success',
    3: 'info'
  }
  return types[type] || 'info'
}

const getStatusLabel = (status: number) => {
  const labels = {
    0: t('crm.marketing.campaign.status.draft'),
    1: t('crm.marketing.campaign.status.active'),
    2: t('crm.marketing.campaign.status.completed'),
    3: t('crm.marketing.campaign.status.paused')
  }
  return labels[status] || status
}

const getStatusTagType = (status: number) => {
  const types = {
    0: 'info',
    1: 'success',
    2: 'default',
    3: 'warning'
  }
  return types[status] || 'info'
}

onMounted(() => {
  getList()
})
</script>