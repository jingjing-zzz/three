<template>
  <ContentWrap>
    <el-form
      class="-mb-15px"
      :model="queryParams"
      ref="queryFormRef"
      label-width="auto"
    >
      <el-row :gutter="20">
        <el-col :span="8">
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
        <el-col :span="8">
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
        <el-col :span="8">
          <el-form-item label="时间范围" prop="createTime">
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
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <ContentWrap>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ statistics.total }}</div>
            <div class="stat-label">总工单数</div>
          </div>
          <div class="stat-icon bg-primary"><Icon icon="ep:document" /></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ statistics.pending }}</div>
            <div class="stat-label">待处理</div>
          </div>
          <div class="stat-icon bg-info"><Icon icon="ep:clock" /></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ statistics.processing }}</div>
            <div class="stat-label">处理中</div>
          </div>
          <div class="stat-icon bg-warning"><Icon icon="ep:loading" /></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ statistics.completed }}</div>
            <div class="stat-label">已完结</div>
          </div>
          <div class="stat-icon bg-success"><Icon icon="ep:check-circle" /></div>
        </el-card>
      </el-col>
    </el-row>
  </ContentWrap>

  <ContentWrap>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card header="工单状态分布">
          <div ref="statusChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="工单类型分布">
          <div ref="typeChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card header="工单优先级分布">
          <div ref="priorityChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="工单趋势">
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </ContentWrap>
</template>
<script lang="ts" setup>
import * as WorkOrderApi from '@/api/system/work-order'

defineOptions({ name: 'CrmWorkOrderStatistics' })

const { t } = useI18n()
const message = useMessage()

const typeEnumList = ref<WorkOrderApi.EnumVO[]>([])
const priorityEnumList = ref<WorkOrderApi.EnumVO[]>([])

const loadEnums = async () => {
  typeEnumList.value = await WorkOrderApi.getWorkOrderTypeEnum()
  priorityEnumList.value = await WorkOrderApi.getWorkOrderPriorityEnum()
}

const queryParams = reactive({
  type: undefined as number | undefined,
  priority: undefined as number | undefined,
  createTime: undefined as string[] | undefined
})
const queryFormRef = ref()

const statistics = reactive({
  total: 0,
  pending: 0,
  processing: 0,
  completed: 0,
  rejected: 0
})

const statusChartRef = ref()
const typeChartRef = ref()
const priorityChartRef = ref()
const trendChartRef = ref()

const getStatistics = async () => {
  const params: WorkOrderApi.WorkOrderStatisticsReqVO = {
    type: queryParams.type,
    priority: queryParams.priority
  }
  if (queryParams.createTime && queryParams.createTime.length === 2) {
    params.createTimeBegin = queryParams.createTime[0]
    params.createTimeEnd = queryParams.createTime[1]
  }
  const data = await WorkOrderApi.getWorkOrderStatistics(params)
  
  statistics.total = data.total || 0
  statistics.pending = data.pending || 0
  statistics.processing = data.processing || 0
  statistics.completed = data.completed || 0
  statistics.rejected = data.rejected || 0

  renderCharts(data)
}

const renderCharts = (data: WorkOrderApi.WorkOrderStatisticsVO) => {
  renderStatusChart(data.statusDistribution || [])
  renderTypeChart(data.typeDistribution || [])
  renderPriorityChart(data.priorityDistribution || [])
  renderTrendChart(data.trendData || [])
}

const renderStatusChart = (distribution: Array<{ status: number; statusName: string; count: number; percentage: number }>) => {
  const container = statusChartRef.value
  if (!container) return
  
  const sorted = distribution.sort((a, b) => a.status - b.status)
  const colors = ['#909399', '#409EFF', '#67C23A', '#F56C6C']
  let gradientParts = []
  let currentAngle = 0
  
  sorted.forEach((item, index) => {
    const nextAngle = currentAngle + item.percentage * 3.6
    gradientParts.push(`${colors[index % colors.length]} ${currentAngle}deg ${nextAngle}deg`)
    currentAngle = nextAngle
  })
  
  const legendItems = sorted.map((item, index) => 
    `<span><span style="display:inline-block;width:12px;height:12px;border-radius:50%;background:${colors[index % colors.length]};margin-right:5px;"></span>${item.statusName}(${item.count})</span>`
  ).join('')
  
  container.innerHTML = `
    <div style="display: flex; justify-content: center; align-items: center; height: 250px;">
      <div style="width: 200px; height: 200px; border-radius: 50%; background: conic-gradient(${gradientParts.join(', ')});"></div>
    </div>
    <div style="display: flex; justify-content: center; gap: 20px; margin-top: 10px;">${legendItems}</div>
  `
}

const renderTypeChart = (distribution: Array<{ type: number; typeName: string; count: number }>) => {
  const container = typeChartRef.value
  if (!container) return
  
  const maxCount = Math.max(...distribution.map(d => d.count), 1)
  const bars = distribution.map(item => {
    const width = (item.count / maxCount) * 100
    return `
      <div style="margin-bottom: 15px;">
        <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
          <span>${item.typeName}</span>
          <span>${item.count}</span>
        </div>
        <div style="height: 20px; background: #f5f7fa; border-radius: 4px; overflow: hidden;">
          <div style="height: 100%; background: linear-gradient(90deg, #667eea 0%, #764ba2 100%); width: ${width}%;"></div>
        </div>
      </div>
    `
  }).join('')
  
  container.innerHTML = `<div style="padding: 10px;">${bars}</div>`
}

const renderPriorityChart = (distribution: Array<{ priority: number; priorityName: string; count: number }>) => {
  const container = priorityChartRef.value
  if (!container) return
  
  const maxCount = Math.max(...distribution.map(d => d.count), 1)
  const bars = distribution.map(item => {
    const color = item.priority === 1 ? '#F56C6C' : item.priority === 2 ? '#E6A23C' : '#409EFF'
    const width = (item.count / maxCount) * 100
    return `
      <div style="margin-bottom: 15px;">
        <div style="display: flex; justify-content: space-between; margin-bottom: 5px;">
          <span>${item.priorityName}</span>
          <span>${item.count}</span>
        </div>
        <div style="height: 20px; background: #f5f7fa; border-radius: 4px; overflow: hidden;">
          <div style="height: 100%; background: ${color}; width: ${width}%;"></div>
        </div>
      </div>
    `
  }).join('')
  
  container.innerHTML = `<div style="padding: 10px;">${bars}</div>`
}

const renderTrendChart = (trendData: Array<{ date: string; count: number }>) => {
  const container = trendChartRef.value
  if (!container) return
  
  const maxCount = Math.max(...trendData.map(d => d.count), 1)
  
  const bars = trendData.map(item => {
    const height = (item.count / maxCount) * 100
    const monthDay = item.date.slice(4)
    return `
      <div style="flex: 1; display: flex; flex-direction: column; align-items: center;">
        <div style="height: 150px; display: flex; align-items: flex-end; width: 100%; justify-content: center;">
          <div style="width: 30px; background: linear-gradient(180deg, #667eea 0%, #764ba2 100%); border-radius: 4px 4px 0 0; height: ${height}%;"></div>
        </div>
        <span style="font-size: 10px; margin-top: 5px;">${monthDay}</span>
      </div>
    `
  }).join('')
  
  container.innerHTML = `<div style="display: flex; height: 180px;">${bars}</div>`
}

const handleQuery = () => {
  getStatistics()
}

const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}

onMounted(() => {
  loadEnums()
  getStatistics()
})
</script>
<style scoped>
.stat-card {
  position: relative;
  overflow: hidden;
}
.stat-content {
  position: relative;
  z-index: 1;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}
.stat-icon {
  position: absolute;
  right: 20px;
  top: 20px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
}
.bg-primary { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.bg-info { background: linear-gradient(135deg, #909399 0%, #a6a9ad 100%); }
.bg-warning { background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%); }
.bg-success { background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%); }
.chart-container {
  height: 280px;
}
</style>