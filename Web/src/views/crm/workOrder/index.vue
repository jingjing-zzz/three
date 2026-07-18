<template>
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :model="queryParams"
      class="-mb-15px"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="6">
          <el-form-item label="工单标题" prop="title">
            <el-input
              v-model="queryParams.title"
              class="!w-240px"
              clearable
              placeholder="请输入工单标题"
              @keyup.enter="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="工单类型" prop="type">
            <el-select v-model="queryParams.type" class="!w-200px" clearable placeholder="请选择工单类型">
              <el-option v-for="item in WORK_ORDER_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="优先级" prop="priority">
            <el-select v-model="queryParams.priority" class="!w-200px" clearable placeholder="请选择优先级">
              <el-option v-for="item in WORK_ORDER_PRIORITY_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" class="!w-200px" clearable placeholder="请选择状态">
              <el-option v-for="item in WORK_ORDER_STATUS_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
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
            <el-button v-hasPermi="['crm:work-order:create']" type="primary" @click="openForm('create')">
              <Icon class="mr-5px" icon="ep:plus" />
              {{ t('common.add') }}
            </el-button>
            <el-button
              v-hasPermi="['crm:work-order:query']"
              plain
              type="info"
              @click="goStatistics"
            >
              <Icon class="mr-5px" icon="ep:data-analysis" />
              工单统计
            </el-button>
            <el-button
              v-hasPermi="['crm:work-order:export']"
              :loading="exportLoading"
              plain
              type="success"
              @click="handleExport"
            >
              <Icon class="mr-5px" icon="ep:download" />
              {{ t('common.export') }}
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <div class="table-scroll-container">
      <el-table v-loading="loading" :data="list" :show-overflow-tooltip="true" :stripe="true" :table-layout="'auto'">
        <el-table-column align="center" label="工单标题" prop="title" min-width="200">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.title }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" label="工单类型" prop="typeName" min-width="100" />
      <el-table-column align="center" label="优先级" prop="priorityName" min-width="100">
        <template #default="scope">
          <el-tag :type="getPriorityTagType(scope.row.priority)" size="small">
            {{ scope.row.priorityName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" prop="statusName" min-width="100">
        <template #default="scope">
          <el-tag :type="getStatusTagType(scope.row.status)" size="small">
            {{ scope.row.statusName }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column align="center" label="客户" prop="customerName" min-width="120" />
      <el-table-column align="center" label="处理人" prop="ownerUserName" min-width="100" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="创建时间"
        prop="createTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        label="更新时间"
        prop="updateTime"
        min-width="180"
      />
      <el-table-column align="center" label="创建人" prop="creatorName" min-width="100" />
      <el-table-column align="center" label="操作" min-width="350">
        <template #default="scope">
          <el-button
            v-hasPermi="['crm:work-order:update']"
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
          >
            {{ t('common.edit') }}
          </el-button>
          <el-button
            v-if="scope.row.status === 1"
            v-hasPermi="['crm:work-order:update']"
            link
            type="success"
            @click="handleStartProcess(scope.row.id)"
          >
            处理
          </el-button>
          <el-button
            v-if="scope.row.status === 2"
            v-hasPermi="['crm:work-order:update']"
            link
            type="success"
            @click="openCompleteDialog(scope.row.id)"
          >
            完结
          </el-button>
          <el-button
            v-if="scope.row.status === 2"
            v-hasPermi="['crm:work-order:update']"
            link
            type="warning"
            @click="openRejectDialog(scope.row.id)"
          >
            退回
          </el-button>
          <el-button
            v-if="scope.row.status === 4"
            v-hasPermi="['crm:work-order:update']"
            link
            type="primary"
            @click="handleRestart(scope.row.id)"
          >
            重新发起
          </el-button>
          <el-button
            v-hasPermi="['crm:work-order:delete']"
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
          >
            {{ t('common.del') }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    </div>
    <Pagination
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNo"
      :total="total"
      @pagination="getList"
    />
  </ContentWrap>

  <WorkOrderForm ref="formRef" @success="getList" />

  <el-dialog v-model="completeDialogVisible" title="完结工单" width="500px">
    <el-form ref="completeFormRef" :model="completeForm" label-width="auto">
      <el-form-item label="处理结果" prop="result">
        <el-input type="textarea" v-model="completeForm.result" placeholder="请输入处理结果" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="completeDialogVisible = false">{{ t('common.cancel') }}</el-button>
      <el-button type="primary" @click="handleComplete">{{ t('common.confirm') }}</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="rejectDialogVisible" title="退回工单" width="500px">
    <el-form ref="rejectFormRef" :model="rejectForm" label-width="auto">
      <el-form-item label="退回原因" prop="rejectReason">
        <el-input type="textarea" v-model="rejectForm.rejectReason" placeholder="请输入退回原因" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="rejectDialogVisible = false">{{ t('common.cancel') }}</el-button>
      <el-button type="primary" @click="handleReject">{{ t('common.confirm') }}</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { dateFormatter } from '@/utils/formatTime'
import download from '@/utils/download'
import * as WorkOrderApi from '@/api/crm/workOrder'
import { WORK_ORDER_TYPE_OPTIONS, WORK_ORDER_PRIORITY_OPTIONS, WORK_ORDER_STATUS_OPTIONS } from '@/api/crm/workOrder'
import WorkOrderForm from './WorkOrderForm.vue'

defineOptions({ name: 'CrmWorkOrder' })

const message = useMessage()
const { t } = useI18n()
const loading = ref(true)
const total = ref(0)
const list = ref<any[]>([])
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  title: null,
  type: null,
  priority: null,
  status: null
})
const queryFormRef = ref()

const completeDialogVisible = ref(false)
const completeFormRef = ref()
const completeForm = reactive({
  id: null as number | null,
  result: ''
})

const rejectDialogVisible = ref(false)
const rejectFormRef = ref()
const rejectForm = reactive({
  id: null as number | null,
  rejectReason: ''
})

const getPriorityTagType = (priority: number) => {
  switch (priority) {
    case 1: return 'danger'
    case 2: return 'warning'
    case 3: return 'info'
    case 4: return 'success'
    default: return 'info'
  }
}

const getStatusTagType = (status: number) => {
  switch (status) {
    case 1: return 'warning'
    case 2: return 'primary'
    case 3: return 'success'
    case 4: return 'danger'
    default: return 'info'
  }
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
  queryFormRef.value.resetFields()
  handleQuery()
}

const { push } = useRouter()
const openDetail = (id: number) => {
  push({ name: 'CrmWorkOrderDetail', params: { id } })
}

const goStatistics = () => {
  push({ name: 'CrmWorkOrderStatistics' })
}

const exportLoading = ref(false)

const handleExport = async () => {
  try {
    await message.exportConfirm()
    exportLoading.value = true
    const data = await WorkOrderApi.exportWorkOrder(queryParams)
    download.excel(data, t('exportFileName') + '.xls')
  } catch {
  } finally {
    exportLoading.value = false
  }
}

const formRef = ref()
const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const handleDelete = async (id: number) => {
  try {
    await message.delConfirm()
    await WorkOrderApi.deleteWorkOrder(id)
    message.success(t('common.delSuccess'))
    await getList()
  } catch {}
}

const handleStartProcess = async (id: number) => {
  try {
    await WorkOrderApi.updateWorkOrderStatus({ id, status: 2 })
    message.success('已开始处理')
    await getList()
  } catch (e) {
    message.error('操作失败')
  }
}

const openCompleteDialog = (id: number) => {
  completeForm.id = id
  completeForm.result = ''
  completeDialogVisible.value = true
}

const handleComplete = async () => {
  try {
    await WorkOrderApi.updateWorkOrderStatus({
      id: completeForm.id!,
      status: 3,
      result: completeForm.result
    })
    message.success('工单已完结')
    completeDialogVisible.value = false
    await getList()
  } catch (e) {
    message.error('操作失败')
  }
}

const openRejectDialog = (id: number) => {
  rejectForm.id = id
  rejectForm.rejectReason = ''
  rejectDialogVisible.value = true
}

const handleReject = async () => {
  try {
    await WorkOrderApi.updateWorkOrderStatus({
      id: rejectForm.id!,
      status: 4,
      rejectReason: rejectForm.rejectReason
    })
    message.success('工单已退回')
    rejectDialogVisible.value = false
    await getList()
  } catch (e) {
    message.error('操作失败')
  }
}

const handleRestart = async (id: number) => {
  try {
    await WorkOrderApi.updateWorkOrderStatus({ id, status: 1 })
    message.success('工单已重新发起')
    await getList()
  } catch (e) {
    message.error('操作失败')
  }
}

onMounted(() => {
  getList()
})
</script>