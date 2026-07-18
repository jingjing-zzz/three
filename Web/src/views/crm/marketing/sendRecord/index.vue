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
          <el-form-item :label="t('crm.marketing.sendRecord.type')" prop="targetType">
            <el-select
              v-model="queryParams.targetType"
              class="!w-240px"
              clearable
              :placeholder="t('common.selectText')"
            >
              <el-option :label="t('crm.marketing.sendRecord.typeSms')" :value="1" />
              <el-option :label="t('crm.marketing.sendRecord.typeEmail')" :value="2" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('crm.marketing.sendRecord.status')" prop="status">
            <el-select
              v-model="queryParams.status"
              class="!w-240px"
              clearable
              :placeholder="t('common.selectText')"
            >
              <el-option :label="t('crm.marketing.sendRecord.statusPending')" :value="0" />
              <el-option :label="t('crm.marketing.sendRecord.statusSuccess')" :value="1" />
              <el-option :label="t('crm.marketing.sendRecord.statusFailed')" :value="2" />
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
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
      <el-table-column align="center" :label="t('crm.marketing.sendRecord.campaignName')" prop="campaignName" min-width="160" />
      <el-table-column align="center" :label="t('crm.marketing.sendRecord.batchName')" prop="batchName" min-width="140" />
      <el-table-column align="center" :label="t('crm.marketing.sendRecord.type')" prop="type" min-width="100">
        <template #default="scope">
          {{ scope.row.type === 1 ? t('crm.marketing.sendRecord.typeSms') : t('crm.marketing.sendRecord.typeEmail') }}
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('crm.marketing.sendRecord.target')" prop="target" min-width="140" />
      <el-table-column align="center" :label="t('crm.marketing.sendRecord.content')" prop="content" min-width="200" />
      <el-table-column align="center" :label="t('crm.marketing.sendRecord.status')" prop="status" min-width="100">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">
            {{ getStatusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('crm.marketing.sendRecord.errorMessage')" prop="errorMessage" min-width="150" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('crm.marketing.sendRecord.sendTime')"
        prop="sendTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('common.createTime')"
        prop="createTime"
        min-width="180"
      />
    </el-table>
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import * as MarketingSendRecordApi from '@/api/crm/marketing/sendRecord'

defineOptions({ name: 'MarketingSendRecord' })

const message = useMessage()
const { t } = useI18n()
const loading = ref(true)
const total = ref(0)
const list = ref([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  targetType: null,
  status: null
})
const queryFormRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await MarketingSendRecordApi.getMarketingSendRecordPage(queryParams)
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

const getStatusLabel = (status: number) => {
  const labels = {
    0: t('crm.marketing.sendRecord.statusPending'),
    1: t('crm.marketing.sendRecord.statusSuccess'),
    2: t('crm.marketing.sendRecord.statusFailed')
  }
  return labels[status] || status
}

const getStatusTagType = (status: number) => {
  const types = {
    0: 'info',
    1: 'success',
    2: 'danger'
  }
  return types[status] || 'info'
}

onMounted(() => {
  getList()
})
</script>