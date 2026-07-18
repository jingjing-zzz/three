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
          <el-form-item :label="t('oa.workReport.type')" prop="type">
            <el-select
              v-model="queryParams.type"
              class="!w-240px"
              clearable
              :placeholder="t('oa.workReport.typePlaceholder')"
            >
              <el-option label="日报" :value="1" />
              <el-option label="周报" :value="2" />
              <el-option label="月报" :value="3" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item :label="t('oa.workReport.createTime')" prop="createTime">
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
          <el-form-item :label="t('oa.workReport.status')" prop="status">
            <el-select
              v-model="queryParams.status"
              class="!w-240px"
              clearable
              :placeholder="t('oa.workReport.statusPlaceholder')"
            >
              <el-option label="草稿" :value="0" />
              <el-option label="已提交" :value="1" />
              <el-option label="已审批" :value="2" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item :label="t('oa.workReport.reportTitle')" prop="title">
            <el-input
              v-model="queryParams.title"
              class="!w-240px"
              clearable
              :placeholder="t('oa.workReport.reportTitlePlaceholder')"
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
              {{ t('oa.workReport.createWorkReport') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column align="center" :label="t('oa.workReport.id')" prop="id" />
      <el-table-column align="center" :label="t('oa.workReport.type')" prop="type">
        <template #default="scope">
          <span v-if="scope.row.type === 1">日报</span>
          <span v-else-if="scope.row.type === 2">周报</span>
          <span v-else>月报</span>
        </template>
      </el-table-column>
      <el-table-column align="center" :label="t('oa.workReport.reportTitle')" prop="title" />
      <el-table-column align="center" :label="t('oa.workReport.reportDate')" prop="reportDate" />
      <el-table-column align="center" :label="t('oa.workReport.status')" prop="status">
        <template #default="scope">
          <span v-if="scope.row.status === 0">草稿</span>
          <span v-else-if="scope.row.status === 1">已提交</span>
          <span v-else>已审批</span>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('oa.workReport.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('common.operation')" min-width="200">
        <template #default="scope">
          <el-button
            v-hasPermi="['bpm:oa-work-report:query']"
            link
            type="primary"
            @click="handleDetail(scope.row)"
          >
            {{ t('common.detail') }}
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
import { dateFormatter } from '@/utils/formatTime'
import * as WorkReportApi from '@/api/bpm/workReport'

defineOptions({ name: 'BpmOAWorkReport' })

const router = useRouter()
const { t } = useI18n('bpm')

const loading = ref(true)
const total = ref(0)
const list = ref([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  type: undefined,
  status: undefined,
  title: undefined,
  createTime: []
})
const queryFormRef = ref()

const getList = async () => {
  loading.value = true
  try {
    const data = await WorkReportApi.getWorkReportPage(queryParams)
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
  router.push({ name: 'OAWorkReportCreate' })
}

const handleDetail = (row: WorkReportApi.WorkReportVO) => {
  router.push({
    name: 'OAWorkReportDetail',
    query: {
      id: row.id
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