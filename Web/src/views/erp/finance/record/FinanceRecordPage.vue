<template>
  <ContentWrap>
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" :inline="true" label-width="80px">
      <el-form-item label="单据编号" prop="no">
        <el-input v-model="queryParams.no" placeholder="请输入单据编号" clearable @keyup.enter="handleQuery" class="!w-220px" />
      </el-form-item>
      <el-form-item label="主题" prop="subject">
        <el-input v-model="queryParams.subject" placeholder="请输入主题" clearable @keyup.enter="handleQuery" class="!w-220px" />
      </el-form-item>
      <el-form-item label="业务时间" prop="recordTime">
        <el-date-picker
          v-model="queryParams.recordTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-240px"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" clearable placeholder="请选择状态" class="!w-160px">
          <el-option label="未审核" :value="10" />
          <el-option label="已审核" :value="20" />
        </el-select>
      </el-form-item>
      <el-form-item label="逾期" prop="overdue">
        <el-select v-model="queryParams.overdue" clearable placeholder="请选择" class="!w-140px">
          <el-option label="是" :value="true" />
          <el-option label="否" :value="false" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" /> 搜索</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" /> 重置</el-button>
        <el-button type="primary" plain @click="openForm('create')" v-hasPermi="['erp:finance-record:create']">
          <Icon icon="ep:plus" class="mr-5px" /> 新增
        </el-button>
        <el-button type="warning" plain @click="handleMarkOverdue" v-hasPermi="['erp:finance-record:update-status']">
          <Icon icon="ep:clock" class="mr-5px" /> 逾期检测
        </el-button>
        <el-button type="success" plain @click="handleExport" :loading="exportLoading" v-hasPermi="['erp:finance-record:export']">
          <Icon icon="ep:download" class="mr-5px" /> 导出
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true" :table-layout="'auto'">
      <el-table-column label="单据编号" align="center" prop="no" min-width="160" />
      <el-table-column label="主题" align="center" prop="subject" min-width="180" />
      <el-table-column label="往来单位" align="center" prop="counterparty" min-width="140" />
      <el-table-column label="发票号码" align="center" prop="invoiceNo" min-width="140" />
      <el-table-column label="价税合计" align="center" prop="totalAmount" min-width="110" />
      <el-table-column label="结算账户" align="center" prop="accountName" min-width="120" />
      <el-table-column label="申请人" align="center" prop="applicantUserName" min-width="100" />
      <el-table-column label="财务人员" align="center" prop="financeUserName" min-width="100" />
      <el-table-column label="状态" align="center" prop="status" min-width="90">
        <template #default="scope">
          <el-tag :type="scope.row.status === 20 ? 'success' : 'info'">
            {{ scope.row.status === 20 ? '已审核' : '未审核' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="逾期" align="center" prop="overdue" min-width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.overdue" type="danger">是</el-tag>
          <el-tag v-else type="success">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="业务时间" align="center" prop="recordTime" :formatter="dateFormatter" min-width="180" />
      <el-table-column label="操作" align="center" fixed="right" width="220">
        <template #default="scope">
          <el-button link type="primary" @click="openForm('update', scope.row.id)" v-hasPermi="['erp:finance-record:update']">
            编辑
          </el-button>
          <el-button
            link
            :type="scope.row.status === 20 ? 'warning' : 'success'"
            @click="handleStatus(scope.row)"
            v-hasPermi="['erp:finance-record:update-status']"
          >
            {{ scope.row.status === 20 ? '反审核' : '审核' }}
          </el-button>
          <el-button link type="danger" @click="handleDelete(scope.row.id)" v-hasPermi="['erp:finance-record:delete']">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" v-model:page="queryParams.pageNo" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </ContentWrap>

  <FinanceRecordForm ref="formRef" :type="type" :title="title" @success="getList" />
</template>

<script setup lang="ts">
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import { FinanceRecordApi, FinanceRecordVO } from '@/api/erp/finance/record'
import FinanceRecordForm from './FinanceRecordForm.vue'

defineOptions({ name: 'ErpFinanceRecordPage' })

const props = defineProps<{ type: number; title: string }>()
const message = useMessage()

const loading = ref(true)
const list = ref<FinanceRecordVO[]>([])
const total = ref(0)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  type: props.type,
  no: undefined,
  subject: undefined,
  status: undefined,
  recordTime: undefined,
  overdue: undefined
})
const queryFormRef = ref()
const exportLoading = ref(false)

const getList = async () => {
  loading.value = true
  try {
    const data = await FinanceRecordApi.getPage(queryParams)
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
  queryParams.type = props.type
  handleQuery()
}

const formRef = ref()
const openForm = (formType: string, id?: number) => {
  formRef.value.open(formType, id)
}

const handleStatus = async (row: FinanceRecordVO) => {
  const nextStatus = row.status === 20 ? 10 : 20
  await message.confirm(`确认${nextStatus === 20 ? '审核' : '反审核'}该单据吗？`)
  await FinanceRecordApi.updateStatus(row.id!, nextStatus)
  message.success('操作成功')
  await getList()
}

const handleMarkOverdue = async () => {
  const count = await FinanceRecordApi.markOverdue()
  message.success(`已标记 ${count} 条逾期单据`)
  await getList()
}

const handleDelete = async (id: number) => {
  await message.delConfirm()
  await FinanceRecordApi.delete([id])
  message.success('删除成功')
  await getList()
}

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await FinanceRecordApi.exportExcel(queryParams)
    download.excel(data, `${props.title}.xls`)
  } finally {
    exportLoading.value = false
  }
}

onMounted(() => {
  getList()
})
</script>
