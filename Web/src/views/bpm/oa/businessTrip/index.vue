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
          <el-form-item :label="t('oa.businessTrip.destination')" prop="destination">
            <el-input
              v-model="queryParams.destination"
              class="!w-240px"
              clearable
              :placeholder="t('oa.businessTrip.destinationPlaceholder')"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('oa.businessTrip.applyTime')" prop="createTime">
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
          <el-form-item :label="t('oa.businessTrip.approvalResult')" prop="status">
            <el-select
              v-model="queryParams.status"
              class="!w-240px"
              clearable
              :placeholder="t('oa.businessTrip.approvalResultPlaceholder')"
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
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('oa.businessTrip.reason')" prop="reason">
            <el-input
              v-model="queryParams.reason"
              class="!w-240px"
              clearable
              :placeholder="t('oa.businessTrip.reasonPlaceholder')"
              @keyup.enter="handleQuery"
            />
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
              {{ t('oa.businessTrip.createBusinessTrip') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column align="center" :label="t('oa.businessTrip.applyId')" prop="id" />
      <el-table-column align="center" :label="t('common.status')" prop="status">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.BPM_PROCESS_INSTANCE_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('oa.businessTrip.startTime')"
        prop="startTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('oa.businessTrip.endTime')"
        prop="endTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('oa.businessTrip.day')" prop="day" />
      <el-table-column align="center" :label="t('oa.businessTrip.destination')" prop="destination" />
      <el-table-column align="center" :label="t('oa.businessTrip.reason')" prop="reason" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('oa.businessTrip.applyTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('common.operation')" min-width="200">
        <template #default="scope">
          <el-button
            v-hasPermi="['bpm:oa-business-trip:query']"
            link
            type="primary"
            @click="handleDetail(scope.row)"
          >
            {{ t('common.detail') }}
          </el-button>
          <el-button
            v-if="scope.row.processInstanceId"
            v-hasPermi="['bpm:oa-business-trip:query']"
            link
            type="primary"
            @click="handleProcessDetail(scope.row)"
          >
            {{ t('oa.businessTrip.progress') }}
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
import * as BusinessTripApi from '@/api/bpm/businessTrip'
import * as ProcessInstanceApi from '@/api/bpm/processInstance'

defineOptions({ name: 'BpmOABusinessTrip' })

const message = useMessage()
const router = useRouter()
const { t } = useI18n('bpm')

const loading = ref(true)
const total = ref(0)
const list = ref([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  status: undefined,
  destination: undefined,
  reason: undefined,
  createTime: []
})
const queryFormRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await BusinessTripApi.getBusinessTripPage(queryParams)
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
  router.push({ name: 'OABusinessTripCreate' })
}

const handleDetail = (row: BusinessTripApi.BusinessTripVO) => {
  router.push({
    name: 'OABusinessTripDetail',
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
