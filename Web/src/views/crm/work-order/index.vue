<template>
  <ContentWrap>
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="6">
          <el-form-item label="工单号" prop="orderNo">
            <el-input
              v-model="queryParams.orderNo"
              placeholder="请输入工单号"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="工单标题" prop="title">
            <el-input
              v-model="queryParams.title"
              placeholder="请输入工单标题"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="工单类型" prop="type">
            <el-select
              v-model="queryParams.type"
              placeholder="请选择工单类型"
              clearable
              class="!w-240px"
            >
              <el-option
                v-for="item in typeEnumList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="优先级" prop="priority">
            <el-select
              v-model="queryParams.priority"
              placeholder="请选择优先级"
              clearable
              class="!w-240px"
            >
              <el-option
                v-for="item in priorityEnumList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-form-item label="工单状态" prop="status">
            <el-select
              v-model="queryParams.status"
              placeholder="请选择工单状态"
              clearable
              class="!w-240px"
            >
              <el-option
                v-for="item in statusEnumList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="处理人" prop="assignee">
            <el-input
              v-model="queryParams.assignee"
              placeholder="请输入处理人"
              clearable
              @keyup.enter="handleQuery"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="创建时间" prop="createTime">
            <el-date-picker
              v-model="queryParams.createTime"
              value-format="YYYY-MM-DD HH:mm:ss"
              type="datetimerange"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              class="!w-240px"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item>
            <el-button @click="handleQuery"><Icon icon="ep:search" class="mr-5px" />查询</el-button>
            <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" />重置</el-button>
            <el-button
              type="primary"
              plain
              @click="openForm('create')"
              v-hasPermi="['crm:work-order:create']"
            >
              <Icon icon="ep:plus" class="mr-5px" />新增工单
            </el-button>
            <el-button
              type="danger"
              plain
              :disabled="checkedIds.length === 0"
              @click="handleDeleteBatch"
              v-hasPermi="['crm:work-order:delete']"
            >
              <Icon icon="ep:delete" class="mr-5px" />批量删除
            </el-button>
            <el-button
              type="info"
              plain
              @click="handleStatistics"
              v-hasPermi="['crm:work-order:query']"
            >
              <Icon icon="ep:data-line" class="mr-5px" />工单统计
            </el-button>
            <el-button
              type="success"
              plain
              :loading="exportLoading"
              @click="handleExport"
              v-hasPermi="['crm:work-order:export']"
            >
              <Icon icon="ep:download" class="mr-5px" />导出
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-table v-loading="loading" :data="list" :table-layout="'auto'" @selection-change="handleRowCheckboxChange">
      <el-table-column type="selection" width="55" />
      <el-table-column label="工单号" align="center" prop="orderNo" min-width="160" />
      <el-table-column label="工单标题" align="center" prop="title" min-width="200" />
      <el-table-column label="工单类型" align="center" prop="type" width="100">
        <template #default="scope">
          <el-tag size="small">{{ scope.row.typeName || getTypeLabel(scope.row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="优先级" align="center" prop="priority" width="80">
        <template #default="scope">
          <el-tag :type="getPriorityType(scope.row.priority)" size="small">
            {{ scope.row.priorityName || getPriorityLabel(scope.row.priority) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template #default="scope">
          <el-tag :type="getStatusType(scope.row.status)" size="small">
            {{ scope.row.statusName || getStatusLabel(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发起人" align="center" prop="reporterName" width="100" />
      <el-table-column label="处理人" align="center" prop="assigneeName" width="100" />
      <el-table-column label="客户" align="center" prop="customerName" width="120" />
      <el-table-column label="创建时间" align="center" prop="createTime" min-width="180" :formatter="dateFormatter" />
      <el-table-column label="操作" align="center" width="280">
        <template #default="scope">
          <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['crm:work-order:update']"
          >
            编辑
          </el-button>
          <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['crm:work-order:delete']"
          >
            删除
          </el-button>
          <el-button
            link
            type="success"
            @click="handleProcess(scope.row.id)"
            v-if="scope.row.status === 1"
            v-hasPermi="['crm:work-order:update']"
          >
            处理
          </el-button>
          <el-button
            link
            type="primary"
            @click="handleComplete(scope.row.id)"
            v-if="scope.row.status === 2"
            v-hasPermi="['crm:work-order:update']"
          >
            完结
          </el-button>
          <el-button
            link
            type="warning"
            @click="handleReject(scope.row.id)"
            v-if="scope.row.status !== 4"
            v-hasPermi="['crm:work-order:update']"
          >
            退回
          </el-button>
          <el-button
            link
            type="info"
            @click="handleViewStatusFlow(scope.row.id)"
          >
            流转记录
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>

  <WorkOrderForm ref="formRef" @success="getList" />

  <WorkOrderStatisticsDialog ref="statisticsDialogRef" />

  <Dialog v-model="statusFlowVisible" title="状态流转记录" width="700">
    <el-table :data="statusFlowList" :table-layout="'auto'">
      <el-table-column label="时间" align="center" prop="createTime" min-width="180" :formatter="dateFormatter" />
      <el-table-column label="操作人" align="center" prop="operator" width="100" />
      <el-table-column label="原状态" align="center" prop="fromStatusName" width="100" />
      <el-table-column label="目标状态" align="center" prop="toStatusName" width="100" />
      <el-table-column label="备注" align="center" prop="remark" />
    </el-table>
  </Dialog>
</template>
<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import * as WorkOrderApi from '@/api/system/work-order'
import WorkOrderForm from './WorkOrderForm.vue'
import WorkOrderStatisticsDialog from './WorkOrderStatisticsDialog.vue'

defineOptions({ name: 'CrmWorkOrder' })

const message = useMessage()
const { t } = useI18n()

const loading = ref(true)
const total = ref(0)
const list = ref<WorkOrderApi.WorkOrderVO[]>([])
const queryParams = reactive<WorkOrderApi.WorkOrderPageReqVO>({
  pageNo: 1,
  pageSize: 10,
  orderNo: '',
  title: '',
  type: undefined,
  priority: undefined,
  status: undefined,
  assignee: '',
  createTime: undefined
})
const queryFormRef = ref()

const typeEnumList = ref<WorkOrderApi.EnumVO[]>([])
const priorityEnumList = ref<WorkOrderApi.EnumVO[]>([])
const statusEnumList = ref<WorkOrderApi.EnumVO[]>([])

const loadEnums = async () => {
  typeEnumList.value = await WorkOrderApi.getWorkOrderTypeEnum()
  priorityEnumList.value = await WorkOrderApi.getWorkOrderPriorityEnum()
  statusEnumList.value = await WorkOrderApi.getWorkOrderStatusEnum()
}

const getTypeLabel = (value: number) => {
  const item = typeEnumList.value.find(e => e.value === value)
  return item ? item.label : value
}

const getPriorityLabel = (value: number) => {
  const item = priorityEnumList.value.find(e => e.value === value)
  return item ? item.label : value
}

const getStatusLabel = (value: number) => {
  const item = statusEnumList.value.find(e => e.value === value)
  return item ? item.label : value
}

const getPriorityType = (value: number) => {
  if (value === 1) return 'danger'
  if (value === 2) return 'warning'
  if (value === 3) return 'primary'
  return 'info'
}

const getStatusType = (value: number) => {
  if (value === 1) return 'info'
  if (value === 2) return 'primary'
  if (value === 3) return 'success'
  if (value === 4) return 'danger'
  return 'info'
}

const getList = async () => {
  loading.value = true
  try {
    const data = await WorkOrderApi.getWorkOrderPage(queryParams)
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
  queryFormRef.value?.resetFields()
  handleQuery()
}

const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await WorkOrderApi.deleteWorkOrder(id)
    message.success('删除成功')
    await getList()
  } catch {}
}

const checkedIds = ref<number[]>([])
const handleRowCheckboxChange = (rows: WorkOrderApi.WorkOrderVO[]) => {
  checkedIds.value = rows.map(row => row.id!)
}

const handleDeleteBatch = async () => {
  try {
    await message.delConfirm()
    await WorkOrderApi.deleteWorkOrderList(checkedIds.value)
    checkedIds.value = []
    message.success('批量删除成功')
    await getList()
  } catch {}
}

const handleProcess = async (id: number) => {
  try {
    await message.confirm('确认处理此工单？')
    await WorkOrderApi.processWorkOrder({ id })
    message.success('工单已开始处理')
    await getList()
  } catch {}
}

const handleComplete = async (id: number) => {
  try {
    await message.confirm('确认完结此工单？')
    await WorkOrderApi.completeWorkOrder({ id })
    message.success('工单已完结')
    await getList()
  } catch {}
}

const handleReject = async (id: number) => {
  try {
    await message.confirm('确认退回此工单？')
    await WorkOrderApi.rejectWorkOrder({ id })
    message.success('工单已退回')
    await getList()
  } catch {}
}

const statusFlowVisible = ref(false)
const statusFlowList = ref<WorkOrderApi.WorkOrderStatusFlowVO[]>([])
const handleViewStatusFlow = async (orderId: number) => {
  statusFlowList.value = await WorkOrderApi.getWorkOrderStatusFlow(orderId)
  statusFlowVisible.value = true
}

/** 工单统计 */
const statisticsDialogRef = ref()
const handleStatistics = () => {
  statisticsDialogRef.value?.open()
}

/** 导出按钮操作 */
const exportLoading = ref(false)
const handleExport = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    exportLoading.value = true
    const data = await WorkOrderApi.exportWorkOrder(queryParams)
    download.excel(data, '工单.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

onMounted(() => {
  loadEnums()
  getList()
})
</script>