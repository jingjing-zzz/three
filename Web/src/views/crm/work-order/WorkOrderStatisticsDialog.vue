<template>
  <Dialog v-model="dialogVisible" title="工单统计" width="960px" append-to-body>
    <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="70px">
      <el-form-item label="工单类型" prop="type">
        <el-select v-model="queryParams.type" clearable placeholder="全部类型" class="!w-180px">
          <el-option v-for="item in typeEnumList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="优先级" prop="priority">
        <el-select v-model="queryParams.priority" clearable placeholder="全部优先级" class="!w-180px">
          <el-option v-for="item in priorityEnumList" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="创建时间" prop="createTime">
        <el-date-picker
          v-model="queryParams.createTime"
          type="datetimerange"
          value-format="YYYY-MM-DD HH:mm:ss"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          class="!w-340px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery"><Icon icon="ep:search" class="mr-5px" />查询</el-button>
        <el-button @click="resetQuery"><Icon icon="ep:refresh" class="mr-5px" />重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="12" class="mb-16px">
      <el-col v-for="item in summaryCards" :key="item.label" :span="6">
        <el-card shadow="never" class="text-center">
          <div class="text-gray-500 text-sm">{{ item.label }}</div>
          <div class="text-2xl font-bold mt-8px" :class="item.className">{{ item.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="8">
        <el-card shadow="never" header="状态分布">
          <el-table v-loading="loading" :data="statistics.statusDistribution" size="small" :table-layout="'auto'">
            <el-table-column label="状态" prop="statusName" />
            <el-table-column label="数量" prop="count" width="70" align="right" />
            <el-table-column label="占比" width="80" align="right">
              <template #default="scope">{{ formatPercent(scope.row.percentage) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" header="类型分布">
          <el-table v-loading="loading" :data="statistics.typeDistribution" size="small" :table-layout="'auto'">
            <el-table-column label="类型" prop="typeName" />
            <el-table-column label="数量" prop="count" width="70" align="right" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" header="优先级分布">
          <el-table v-loading="loading" :data="statistics.priorityDistribution" size="small" :table-layout="'auto'">
            <el-table-column label="优先级" prop="priorityName" />
            <el-table-column label="数量" prop="count" width="70" align="right" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" header="近期开单趋势" class="mt-16px">
      <el-table v-loading="loading" :data="statistics.trendData" size="small" :table-layout="'auto'" max-height="220">
        <el-table-column label="日期" prop="date" />
        <el-table-column label="新增工单数" prop="count" align="right" />
      </el-table>
    </el-card>
  </Dialog>
</template>

<script setup lang="ts">
import * as WorkOrderApi from '@/api/system/work-order'

defineOptions({ name: 'WorkOrderStatisticsDialog' })

const dialogVisible = ref(false)
const loading = ref(false)
const queryFormRef = ref()
const typeEnumList = ref<WorkOrderApi.EnumVO[]>([])
const priorityEnumList = ref<WorkOrderApi.EnumVO[]>([])

const queryParams = reactive({
  type: undefined as number | undefined,
  priority: undefined as number | undefined,
  createTime: undefined as string[] | undefined
})

const statistics = reactive<WorkOrderApi.WorkOrderStatisticsVO>({
  total: 0,
  pending: 0,
  processing: 0,
  completed: 0,
  rejected: 0,
  statusDistribution: [],
  typeDistribution: [],
  priorityDistribution: [],
  trendData: []
})

const summaryCards = computed(() => [
  { label: '工单总数', value: statistics.total, className: 'text-primary' },
  { label: '待处理', value: statistics.pending, className: 'text-info' },
  { label: '处理中', value: statistics.processing, className: 'text-warning' },
  { label: '已完成', value: statistics.completed, className: 'text-success' }
])

const loadEnums = async () => {
  ;[typeEnumList.value, priorityEnumList.value] = await Promise.all([
    WorkOrderApi.getWorkOrderTypeEnum(),
    WorkOrderApi.getWorkOrderPriorityEnum()
  ])
}

const getStatistics = async () => {
  loading.value = true
  try {
    const data = await WorkOrderApi.getWorkOrderStatistics({
      type: queryParams.type,
      priority: queryParams.priority,
      createTimeBegin: queryParams.createTime?.[0],
      createTimeEnd: queryParams.createTime?.[1]
    })
    Object.assign(statistics, data)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => getStatistics()

const resetQuery = async () => {
  queryFormRef.value?.resetFields()
  await getStatistics()
}

const formatPercent = (value: number) => `${Number(value || 0).toFixed(1)}%`

const open = async () => {
  dialogVisible.value = true
  await loadEnums()
  await getStatistics()
}

defineExpose({ open })
</script>
