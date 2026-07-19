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
          <el-form-item :label="t('oa.customerVisit.customerName')" prop="customerName">
            <el-input
              v-model="queryParams.customerName"
              class="!w-240px"
              clearable
              :placeholder="t('oa.customerVisit.customerNamePlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('oa.customerVisit.applyTime')" prop="createTime">
            <el-date-picker
              v-model="queryParams.createTime"
              :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
              class="!w-240px"
              :end-placeholder="t('process.instance.endDate')"
              :start-placeholder="t('process.instance.startDate')"
              type="daterange"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('oa.customerVisit.approvalResult')" prop="status">
            <el-select
              v-model="queryParams.status"
              class="!w-240px"
              clearable
              :placeholder="t('oa.customerVisit.approvalResultPlaceholder')"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
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
            <el-button plain type="primary" @click="handleCreate()">
              <Icon class="mr-5px" icon="ep:plus" />
              {{ t('oa.customerVisit.createCustomerVisit') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column align="center" :label="t('oa.customerVisit.applyId')" prop="id" />
      <el-table-column align="center" :label="t('common.status')" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('oa.customerVisit.customerName')" prop="customerName" />
      <el-table-column align="center" :label="t('oa.customerVisit.customerContact')" prop="customerContact" />
      <el-table-column align="center" :label="t('oa.customerVisit.contactPhone')" prop="contactPhone" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('oa.customerVisit.visitTime')"
        prop="visitTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('oa.customerVisit.purpose')" prop="purpose" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('oa.customerVisit.applyTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('common.operation')" min-width="200">
        <template #default="scope">
          <el-button
            v-hasPermi="['bpm:oa-customer-visit:query']"
            link
            type="primary"
            @click="handleDetail(scope.row)"
          >
            {{ t('common.detail') }}
          </el-button>
          <el-button
            v-if="scope.row.processInstanceId"
            v-hasPermi="['bpm:oa-customer-visit:query']"
            link
            type="primary"
            @click="handleProcessDetail(scope.row)"
          >
            {{ t('oa.customerVisit.progress') }}
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
</template>
<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as CustomerVisitApi from '@/api/bpm/customerVisit'

defineOptions({ name: 'BpmOACustomerVisit' })

const router = useRouter()
const { t } = useI18n('bpm')

const loading = ref(true)
const total = ref(0)
const list = ref([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  status: undefined,
  customerName: undefined,
  createTime: []
})
const queryFormRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await CustomerVisitApi.getCustomerVisitPage(queryParams)
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

const handleCreate = () => {
  router.push({ name: 'OACustomerVisitCreate' })
}

const handleDetail = (row: CustomerVisitApi.CustomerVisitVO) => {
  router.push({
    name: 'OACustomerVisitDetail',
    query: {
      id: row.id
    }
  })
}

const handleProcessDetail = (row) => {
  router.push({
    name: 'BpmProcessInstanceDetail',
    query: {
      id: row.processInstanceId
    }
  })
}

watch(
  () => router.currentRoute.value,
  () => {
    getList()
  }
)

onMounted(() => {
  getList()
})
</script>
