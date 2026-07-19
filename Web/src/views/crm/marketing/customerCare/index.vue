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
          <el-form-item :label="t('crm.marketing.customerCare.name')" prop="name">
            <el-input
              v-model="queryParams.name"
              class="!w-240px"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.marketing.customerCare.type.label')" prop="type">
            <el-select
              v-model="queryParams.type"
              class="!w-240px"
              clearable
              :placeholder="t('common.selectText')"
            >
              <el-option :label="t('crm.marketing.customerCare.type.birthday')" :value="1" />
              <el-option :label="t('crm.marketing.customerCare.type.anniversary')" :value="2" />
              <el-option :label="t('crm.marketing.customerCare.type.custom')" :value="3" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.marketing.customerCare.status.label')" prop="status">
            <el-select
              v-model="queryParams.status"
              class="!w-240px"
              clearable
              :placeholder="t('common.selectText')"
            >
              <el-option :label="t('crm.marketing.customerCare.status.enabled')" :value="1" />
              <el-option :label="t('crm.marketing.customerCare.status.disabled')" :value="0" />
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
      <el-table-column align="center" :label="t('crm.marketing.customerCare.name')" prop="name" min-width="160">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('crm.marketing.customerCare.type.label')" prop="type" min-width="120">
        <template #default="scope">
          {{ getTypeLabel(scope.row.type) }}
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('crm.marketing.customerCare.sendChannel.label')" prop="sendChannel" min-width="120">
        <template #default="scope">
          {{ getChannelLabel(scope.row.sendChannel) }}
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('crm.marketing.customerCare.content')" prop="content" min-width="200" />
      <el-table-column align="center" :label="t('crm.marketing.customerCare.status.label')" prop="status" min-width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? t('crm.marketing.customerCare.status.enabled') : t('crm.marketing.customerCare.status.disabled') }}
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

  <MarketingCustomerCareForm ref="formRef" @success="getList" />
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as MarketingCustomerCareApi from '@/api/crm/marketing/customerCare'
import MarketingCustomerCareForm from './MarketingCustomerCareForm.vue'

defineOptions({ name: 'MarketingCustomerCare' })

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
    const data = await MarketingCustomerCareApi.getMarketingCustomerCarePage(queryParams)
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

const openDetail = (id: number) => {
  openForm('update', id)
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await MarketingCustomerCareApi.deleteMarketingCustomerCare(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const getTypeLabel = (type: number) => {
  const labels = {
    1: t('crm.marketing.customerCare.type.birthday'),
    2: t('crm.marketing.customerCare.type.anniversary'),
    3: t('crm.marketing.customerCare.type.custom')
  }
  return labels[type] || type
}

const getChannelLabel = (channel: number) => {
  const labels = {
    1: t('crm.marketing.customerCare.sendChannel.sms'),
    2: t('crm.marketing.customerCare.sendChannel.email'),
    3: t('crm.marketing.customerCare.sendChannel.wechat')
  }
  return labels[channel] || channel
}

onMounted(() => {
  getList()
})
</script>