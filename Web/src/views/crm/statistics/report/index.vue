<template>
  <div class="report-page">
    <!-- 顶部标题区 -->
    <el-card class="page-header-card" shadow="never">
      <div class="page-header">
        <div class="header-title-wrap">
          <span class="header-title-bar"></span>
          <span class="header-title">
            <Icon icon="ep:data-board" :size="22" class="mr-8px" />
            商机综合报表
          </span>
        </div>
        <div class="header-actions">
          <el-button type="primary" plain size="default" :loading="loading" @click="loadData">
            <Icon icon="ep:refresh" class="mr-5px" />
            刷新数据
          </el-button>
        </div>
      </div>
      <div class="header-desc">基于当前所有商机数据，提供状态分布、阶段分布、来源分布、负责人业绩和客户分布的综合视图，辅助销售管理决策。</div>
    </el-card>

    <!-- 筛选区 -->
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="queryParams" size="default">
        <el-form-item label="负责人">
          <el-select
            v-model="queryParams.userId"
            placeholder="全部负责人"
            clearable
            filterable
            style="width: 200px"
            @change="handleQuery"
          >
            <el-option
              v-for="user in userList"
              :key="user.id"
              :label="user.nickname"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预计成交时间">
          <el-date-picker
            v-model="queryParams.times"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD HH:mm:ss"
            :shortcuts="dateShortcuts"
            style="width: 360px"
          />
        </el-form-item>
        <el-form-item label="包含已流失">
          <el-checkbox v-model="queryParams.includeLost" @change="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <Icon icon="ep:search" class="mr-5px" />
            搜索
          </el-button>
          <el-button @click="resetQuery">
            <Icon icon="ep:refresh" class="mr-5px" />
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 顶部汇总卡片 -->
    <div class="summary-cards">
      <div class="summary-card gradient-blue">
        <div class="card-decorator"></div>
        <div class="card-icon-wrap">
          <Icon icon="ep:document" :size="24" />
        </div>
        <div class="card-body">
          <div class="card-label">商机总数</div>
          <div class="card-value">{{ overview.totalBusinessCount || 0 }}</div>
          <div class="card-sub">
            <Icon icon="ep:circle-check" :size="12" class="sub-icon" />
            覆盖所有状态
          </div>
        </div>
      </div>
      <div class="summary-card gradient-green">
        <div class="card-decorator"></div>
        <div class="card-icon-wrap">
          <Icon icon="ep:money" :size="24" />
        </div>
        <div class="card-body">
          <div class="card-label">商机总金额</div>
          <div class="card-value">¥{{ formatMoney(overview.totalAmount) }}</div>
          <div class="card-sub">
            <Icon icon="ep:wallet" :size="12" class="sub-icon" />
            全量商机金额
          </div>
        </div>
      </div>
      <div class="summary-card gradient-orange">
        <div class="card-decorator"></div>
        <div class="card-icon-wrap">
          <Icon icon="ep:trend-charts" :size="24" />
        </div>
        <div class="card-body">
          <div class="card-label">预测成交金额</div>
          <div class="card-value">¥{{ formatMoney(overview.forecastAmount) }}</div>
          <div class="card-sub">
            <Icon icon="ep:top" :size="12" class="sub-icon" />
            阶段概率加权
          </div>
        </div>
      </div>
      <div class="summary-card gradient-purple">
        <div class="card-decorator"></div>
        <div class="card-icon-wrap">
          <Icon icon="ep:circle-check-filled" :size="24" />
        </div>
        <div class="card-body">
          <div class="card-label">已成交金额</div>
          <div class="card-value">¥{{ formatMoney(overview.wonAmount) }}</div>
          <div class="card-sub">
            <Icon icon="ep:medal" :size="12" class="sub-icon" />
            赢单商机合计
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区第一行：状态分布、阶段分布 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :md="12">
        <el-card class="chart-card" shadow="never" v-loading="loading">
          <template #header>
            <div class="chart-header">
              <div class="chart-title-wrap">
                <span class="chart-title-bar bar-blue"></span>
                <span class="chart-title">
                  <Icon icon="ep:pie-chart" class="mr-5px" />
                  商机状态分布
                </span>
              </div>
              <el-tag size="small" type="info" effect="plain" round>饼图</el-tag>
            </div>
          </template>
          <Echart v-if="statusChartOption" :options="statusChartOption" :height="320" />
          <el-empty v-else description="暂无数据" :image-size="80" />
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card class="chart-card" shadow="never" v-loading="loading">
          <template #header>
            <div class="chart-header">
              <div class="chart-title-wrap">
                <span class="chart-title-bar bar-green"></span>
                <span class="chart-title">
                  <Icon icon="ep:histogram" class="mr-5px" />
                  商机阶段分布
                </span>
              </div>
              <el-tag size="small" type="success" effect="plain" round>柱状图</el-tag>
            </div>
          </template>
          <Echart v-if="stageChartOption" :options="stageChartOption" :height="320" />
          <el-empty v-else description="暂无数据" :image-size="80" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区第二行：来源分布、客户分布 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :md="12">
        <el-card class="chart-card" shadow="never" v-loading="loading">
          <template #header>
            <div class="chart-header">
              <div class="chart-title-wrap">
                <span class="chart-title-bar bar-orange"></span>
                <span class="chart-title">
                  <Icon icon="ep:rank" class="mr-5px" />
                  商机来源分布
                </span>
              </div>
              <el-tag size="small" type="warning" effect="plain" round>饼图</el-tag>
            </div>
          </template>
          <Echart v-if="sourceChartOption" :options="sourceChartOption" :height="320" />
          <el-empty v-else description="暂无数据" :image-size="80" />
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card class="chart-card" shadow="never" v-loading="loading">
          <template #header>
            <div class="chart-header">
              <div class="chart-title-wrap">
                <span class="chart-title-bar bar-purple"></span>
                <span class="chart-title">
                  <Icon icon="ep:office-building" class="mr-5px" />
                  客户商机分布 Top 10
                </span>
              </div>
              <el-tag size="small" type="danger" effect="plain" round>条形图</el-tag>
            </div>
          </template>
          <Echart v-if="customerChartOption" :options="customerChartOption" :height="320" />
          <el-empty v-else description="暂无数据" :image-size="80" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区第三行：负责人业绩（全宽） -->
    <el-card class="chart-card full-width-card" shadow="never" v-loading="loading">
      <template #header>
        <div class="chart-header">
          <div class="chart-title-wrap">
            <span class="chart-title-bar bar-cyan"></span>
            <span class="chart-title">
              <Icon icon="ep:user-filled" class="mr-5px" />
              负责人业绩对比
            </span>
          </div>
          <el-tag size="small" effect="plain" round>分组柱状图</el-tag>
        </div>
      </template>
      <Echart v-if="ownerChartOption" :options="ownerChartOption" :height="360" />
      <el-empty v-else description="暂无数据" :image-size="80" />
    </el-card>

    <!-- 负责人业绩明细表 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="table-header">
          <div class="chart-title-wrap">
            <span class="chart-title-bar bar-cyan"></span>
            <span class="table-title">
              <Icon icon="ep:list" class="mr-5px" />
              负责人业绩明细
            </span>
          </div>
          <el-tag type="info" effect="plain" round>共 {{ ownerList.length }} 位负责人</el-tag>
        </div>
      </template>
      <el-table :data="ownerList" border stripe>
        <el-table-column label="排名" type="index" width="80" align="center">
          <template #default="scope">
            <el-tag
              :type="scope.$index < 3 ? 'warning' : 'info'"
              effect="dark"
              size="small"
              round
            >
              {{ scope.$index + 1 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="负责人" prop="userName" min-width="120">
          <template #default="scope">
            <div class="owner-cell">
              <Icon icon="ep:user" :size="14" class="mr-5px" />
              {{ scope.row.userName }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="商机数" prop="businessCount" width="100" align="center">
          <template #default="scope">
            <el-tag type="primary" effect="plain" round>{{ scope.row.businessCount }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="商机总金额" prop="totalAmount" width="160" align="right">
          <template #default="scope">¥{{ formatMoney(scope.row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column label="成交金额" prop="wonAmount" width="160" align="right">
          <template #default="scope">
            <span class="won-amount">¥{{ formatMoney(scope.row.wonAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="预测金额" prop="forecastAmount" width="160" align="right">
          <template #default="scope">
            <span class="forecast-amount">¥{{ formatMoney(scope.row.forecastAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="赢率" width="160" align="center">
          <template #default="scope">
            <el-progress
              :percentage="calcWinRate(scope.row)"
              :stroke-width="8"
              :color="getProgressColor(calcWinRate(scope.row))"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import * as DictDataApi from '@/api/system/dict/dict.data'
import * as UserApi from '@/api/system/user'
import { beginOfDay, endOfDay, formatDate } from '@/utils/formatTime'
import { ReportApi, ReportOverviewVO, ReportOwnerItem, ReportReqVO } from '@/api/crm/statistics/report'
import Echart from '@/components/Echart/src/Echart.vue'

defineOptions({ name: 'CrmStatisticsReport' })

const loading = ref(false)
const overview = ref<Partial<ReportOverviewVO>>({})
const ownerList = ref<ReportOwnerItem[]>([])
const sourceDictMap = ref<Map<string, string>>(new Map())
const userList = ref<any[]>([])

const queryParams = reactive<ReportReqVO>({
  userId: undefined,
  times: undefined,
  includeLost: false
})

const dateShortcuts = computed(() => [
  {
    text: '近7天商机',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
      return [start, end]
    }
  },
  {
    text: '近30天商机',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      return [start, end]
    }
  },
  {
    text: '近90天商机',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
      return [start, end]
    }
  },
  {
    text: '近一年商机',
    value: () => {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 365)
      return [start, end]
    }
  }
])

const formatMoney = (value: number | undefined) => {
  if (value == null || isNaN(Number(value))) return '0.00'
  return Number(value).toFixed(2)
}

const calcWinRate = (row: ReportOwnerItem) => {
  if (!row.businessCount || row.businessCount === 0) return 0
  // 赢单率 = 成交金额 / 总金额 × 100
  if (!row.totalAmount || row.totalAmount === 0) return 0
  return Math.min(100, Math.round((row.wonAmount / row.totalAmount) * 100))
}

const getProgressColor = (percent: number) => {
  if (percent < 20) return '#F56C6C'
  if (percent < 40) return '#E6A23C'
  if (percent < 70) return '#409EFF'
  return '#67C23A'
}

/** 加载来源字典 */
const loadSourceDict = async () => {
  try {
    const list = await DictDataApi.getDictDataByType('crm_business_source')
    const map = new Map<string, string>()
    list.forEach((item: any) => {
      map.set(item.value, item.label)
    })
    sourceDictMap.value = map
  } catch (e) {
    // 忽略字典加载失败
  }
}

/** 获取来源名称（从字典翻译） */
const getSourceName = (source: string) => {
  if (!source) return '未填写'
  return sourceDictMap.value.get(source) || source
}

/** 加载报表数据 */
const loadData = async () => {
  loading.value = true
  try {
    await loadSourceDict()
    const params: ReportReqVO = {}
    if (queryParams.userId) params.userId = queryParams.userId
    if (queryParams.times && queryParams.times.length === 2) {
      params.times = [
        formatDate(beginOfDay(new Date(queryParams.times[0]))),
        formatDate(endOfDay(new Date(queryParams.times[1])))
      ]
    }
    if (queryParams.includeLost) params.includeLost = true
    const data = await ReportApi.getReportOverview(params)
    overview.value = data || {}
    ownerList.value = data?.ownerPerformance || []
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  loadData().then(() => rebuildAllCharts())
}

const resetQuery = () => {
  queryParams.userId = undefined
  queryParams.times = undefined
  queryParams.includeLost = false
  loadData().then(() => rebuildAllCharts())
}

const initOptions = async () => {
  userList.value = await UserApi.getSimpleUserList()
}

/** 状态分布饼图 */
const statusChartOption = ref<any>(null)
const buildStatusChart = () => {
  const list = overview.value?.statusDistribution || []
  if (list.length === 0) {
    statusChartOption.value = null
    return
  }
  const colorMap: Record<string, string> = {
    '进行中': '#409EFF',
    '已赢单': '#67C23A',
    '已输单': '#F56C6C'
  }
  statusChartOption.value = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} 个 ({d}%)<br/>金额: ¥{0}',
      extraCssText: 'background:rgba(255,255,255,0.95);border:1px solid #ebeef5;border-radius:8px;box-shadow:0 4px 12px rgba(0,0,0,0.08);padding:8px 12px;'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      itemWidth: 12,
      itemHeight: 12,
      textStyle: { color: '#606266', fontSize: 12 }
    },
    series: [
      {
        name: '商机状态',
        type: 'pie',
        radius: ['45%', '70%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}\n{c} 个',
          fontSize: 12,
          color: '#606266'
        },
        labelLine: { show: true, length: 8, length2: 8 },
        data: list.map(item => ({
          name: item.name,
          value: item.value,
          amount: item.amount,
          itemStyle: { color: colorMap[item.name] || '#909399' }
        }))
      }
    ]
  }
}

/** 阶段分布柱状图 */
const stageChartOption = ref<any>(null)
const buildStageChart = () => {
  const list = overview.value?.stageDistribution || []
  if (list.length === 0) {
    stageChartOption.value = null
    return
  }
  stageChartOption.value = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params: any) => {
        const p = params[0]
        const item = list[p.dataIndex]
        return `${p.name}<br/>商机数: ${p.value} 个<br/>金额: ¥${formatMoney(item?.amount)}<br/>阶段概率: ${item?.percent || 0}%`
      },
      extraCssText: 'background:rgba(255,255,255,0.95);border:1px solid #ebeef5;border-radius:8px;box-shadow:0 4px 12px rgba(0,0,0,0.08);padding:8px 12px;'
    },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '12%', containLabel: true },
    xAxis: {
      type: 'category',
      data: list.map(i => i.name),
      axisLine: { lineStyle: { color: '#DCDFE6' } },
      axisLabel: { color: '#606266', fontSize: 11, interval: 0, rotate: list.length > 5 ? 20 : 0 }
    },
    yAxis: [
      {
        type: 'value',
        name: '商机数',
        axisLine: { lineStyle: { color: '#DCDFE6' } },
        axisLabel: { color: '#909399' },
        splitLine: { lineStyle: { color: '#EBEEF5', type: 'dashed' } }
      }
    ],
    series: [
      {
        name: '商机数',
        type: 'bar',
        barWidth: '45%',
        itemStyle: {
          borderRadius: [6, 6, 0, 0],
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: '#67C23A' },
              { offset: 1, color: '#95D475' }
            ]
          }
        },
        label: { show: true, position: 'top', color: '#606266', fontSize: 11 },
        data: list.map(i => i.value)
      }
    ]
  }
}

/** 来源分布饼图 */
const sourceChartOption = ref<any>(null)
const buildSourceChart = () => {
  const list = overview.value?.sourceDistribution || []
  if (list.length === 0) {
    sourceChartOption.value = null
    return
  }
  const colors = ['#5470C6', '#91CC75', '#FAC858', '#EE6666', '#73C0DE', '#3BA272', '#FC8452', '#9A60B4', '#EA7CCC']
  sourceChartOption.value = {
    tooltip: {
      trigger: 'item',
      formatter: (p: any) => `${p.name}<br/>商机数: ${p.value} 个 (${p.percent}%)<br/>金额: ¥${formatMoney(list[p.dataIndex]?.amount)}`,
      extraCssText: 'background:rgba(255,255,255,0.95);border:1px solid #ebeef5;border-radius:8px;box-shadow:0 4px 12px rgba(0,0,0,0.08);padding:8px 12px;'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      itemWidth: 12,
      itemHeight: 12,
      textStyle: { color: '#606266', fontSize: 12 }
    },
    series: [
      {
        name: '商机来源',
        type: 'pie',
        radius: '65%',
        center: ['40%', '50%'],
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}\n{c} 个',
          fontSize: 12,
          color: '#606266'
        },
        labelLine: { show: true, length: 8, length2: 8 },
        data: list.map((item, idx) => ({
          name: getSourceName(item.name),
          value: item.value,
          itemStyle: { color: colors[idx % colors.length] }
        }))
      }
    ]
  }
}

/** 客户分布条形图 */
const customerChartOption = ref<any>(null)
const buildCustomerChart = () => {
  const list = (overview.value?.customerDistribution || []).slice().reverse()
  if (list.length === 0) {
    customerChartOption.value = null
    return
  }
  customerChartOption.value = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params: any) => {
        const p = params[0]
        const item = list[p.dataIndex]
        return `${p.name}<br/>商机数: ${item.businessCount} 个<br/>金额: ¥${formatMoney(item.totalAmount)}`
      },
      extraCssText: 'background:rgba(255,255,255,0.95);border:1px solid #ebeef5;border-radius:8px;box-shadow:0 4px 12px rgba(0,0,0,0.08);padding:8px 12px;'
    },
    grid: { left: '3%', right: '6%', bottom: '3%', top: '8%', containLabel: true },
    xAxis: {
      type: 'value',
      axisLine: { lineStyle: { color: '#DCDFE6' } },
      axisLabel: { color: '#909399' },
      splitLine: { lineStyle: { color: '#EBEEF5', type: 'dashed' } }
    },
    yAxis: {
      type: 'category',
      data: list.map(i => i.customerName),
      axisLine: { lineStyle: { color: '#DCDFE6' } },
      axisLabel: { color: '#606266', fontSize: 11 }
    },
    series: [
      {
        name: '商机金额',
        type: 'bar',
        barWidth: '55%',
        itemStyle: {
          borderRadius: [0, 6, 6, 0],
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: '#9A60B4' },
              { offset: 1, color: '#C084D8' }
            ]
          }
        },
        label: {
          show: true,
          position: 'right',
          color: '#606266',
          fontSize: 11,
          formatter: (p: any) => `¥${formatMoney(list[p.dataIndex].totalAmount)}`
        },
        data: list.map(i => i.totalAmount)
      }
    ]
  }
}

/** 负责人业绩对比柱状图 */
const ownerChartOption = ref<any>(null)
const buildOwnerChart = () => {
  const list = overview.value?.ownerPerformance || []
  if (list.length === 0) {
    ownerChartOption.value = null
    return
  }
  ownerChartOption.value = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      extraCssText: 'background:rgba(255,255,255,0.95);border:1px solid #ebeef5;border-radius:8px;box-shadow:0 4px 12px rgba(0,0,0,0.08);padding:8px 12px;'
    },
    legend: {
      data: ['商机总金额', '成交金额', '预测金额'],
      top: 0,
      itemWidth: 12,
      itemHeight: 12,
      textStyle: { color: '#606266' }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', top: '15%', containLabel: true },
    xAxis: {
      type: 'category',
      data: list.map(i => i.userName),
      axisLine: { lineStyle: { color: '#DCDFE6' } },
      axisLabel: { color: '#606266', fontSize: 11, interval: 0, rotate: list.length > 5 ? 20 : 0 }
    },
    yAxis: {
      type: 'value',
      name: '金额(¥)',
      axisLine: { lineStyle: { color: '#DCDFE6' } },
      axisLabel: {
        color: '#909399',
        formatter: (v: number) => v >= 10000 ? (v / 10000).toFixed(1) + '万' : v.toString()
      },
      splitLine: { lineStyle: { color: '#EBEEF5', type: 'dashed' } }
    },
    series: [
      {
        name: '商机总金额',
        type: 'bar',
        barGap: '10%',
        barWidth: '20%',
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
          color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [
            { offset: 0, color: '#409EFF' }, { offset: 1, color: '#79BBFF' }
          ] }
        },
        data: list.map(i => i.totalAmount)
      },
      {
        name: '成交金额',
        type: 'bar',
        barWidth: '20%',
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
          color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [
            { offset: 0, color: '#67C23A' }, { offset: 1, color: '#95D475' }
          ] }
        },
        data: list.map(i => i.wonAmount)
      },
      {
        name: '预测金额',
        type: 'bar',
        barWidth: '20%',
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
          color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [
            { offset: 0, color: '#E6A23C' }, { offset: 1, color: '#EEBE77' }
          ] }
        },
        data: list.map(i => i.forecastAmount)
      }
    ]
  }
}

/** 重新构建所有图表 */
const rebuildAllCharts = () => {
  buildStatusChart()
  buildStageChart()
  buildSourceChart()
  buildCustomerChart()
  buildOwnerChart()
}

// 监听数据变化，重新构建图表
watch(() => overview.value, () => {
  rebuildAllCharts()
}, { deep: true })

onMounted(async () => {
  await initOptions()
  await loadData()
  rebuildAllCharts()
})
</script>

<style lang="scss" scoped>
.report-page {
  padding: 16px;
  background: #f5f7fa;
  min-height: calc(100vh - 100px);
}

.page-header-card {
  margin-bottom: 16px;
  border: none;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.04);
}

.filter-card {
  margin-bottom: 16px;
  border: none;
  border-radius: 12px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.04);
}

:deep(.filter-card .el-form-item) {
  margin-bottom: 8px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.header-title-wrap {
  display: flex;
  align-items: center;
}

.header-title-bar {
  width: 4px;
  height: 22px;
  background: linear-gradient(180deg, #409EFF, #79BBFF);
  border-radius: 2px;
  margin-right: 10px;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
}

.header-desc {
  color: #909399;
  font-size: 13px;
  margin-top: 8px;
  padding-left: 14px;
  line-height: 1.6;
}

.summary-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.summary-card {
  position: relative;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  overflow: hidden;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  }

  &.gradient-blue {
    background: linear-gradient(135deg, #ecf5ff 0%, #fff 60%);
    .card-icon-wrap { background: linear-gradient(135deg, #409EFF, #79BBFF); }
    .card-decorator { background: radial-gradient(circle, rgba(64,158,255,0.15), transparent 70%); }
  }
  &.gradient-green {
    background: linear-gradient(135deg, #f0f9eb 0%, #fff 60%);
    .card-icon-wrap { background: linear-gradient(135deg, #67C23A, #95D475); }
    .card-decorator { background: radial-gradient(circle, rgba(103,194,58,0.15), transparent 70%); }
  }
  &.gradient-orange {
    background: linear-gradient(135deg, #fdf6ec 0%, #fff 60%);
    .card-icon-wrap { background: linear-gradient(135deg, #E6A23C, #EEBE77); }
    .card-decorator { background: radial-gradient(circle, rgba(230,162,60,0.15), transparent 70%); }
  }
  &.gradient-purple {
    background: linear-gradient(135deg, #f4f4f5 0%, #fff 60%);
    .card-icon-wrap { background: linear-gradient(135deg, #9A60B4, #C084D8); }
    .card-decorator { background: radial-gradient(circle, rgba(154,96,180,0.15), transparent 70%); }
  }
}

.card-decorator {
  position: absolute;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  right: -30px;
  top: -30px;
}

.card-icon-wrap {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 16px;
  flex-shrink: 0;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.card-body {
  flex: 1;
  z-index: 1;
}

.card-label {
  font-size: 13px;
  color: #606266;
  margin-bottom: 6px;
}

.card-value {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
  margin-bottom: 6px;
}

.card-sub {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
}

.sub-icon {
  margin-right: 4px;
}

.chart-row {
  margin-bottom: 16px;
}

.chart-card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  margin-bottom: 0;
  height: 100%;
}

.full-width-card {
  margin-bottom: 16px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-title-wrap {
  display: flex;
  align-items: center;
}

.chart-title-bar {
  width: 4px;
  height: 16px;
  border-radius: 2px;
  margin-right: 8px;
  background: #409EFF;

  &.bar-blue { background: linear-gradient(180deg, #409EFF, #79BBFF); }
  &.bar-green { background: linear-gradient(180deg, #67C23A, #95D475); }
  &.bar-orange { background: linear-gradient(180deg, #E6A23C, #EEBE77); }
  &.bar-purple { background: linear-gradient(180deg, #9A60B4, #C084D8); }
  &.bar-cyan { background: linear-gradient(180deg, #00C9C9, #5FFFFF); }
}

.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
}

.table-card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
}

.owner-cell {
  display: flex;
  align-items: center;
  color: #303133;
  font-weight: 500;
}

.won-amount {
  color: #67C23A;
  font-weight: 600;
}

.forecast-amount {
  color: #E6A23C;
  font-weight: 600;
}

:deep(.el-card__header) {
  padding: 14px 16px;
  border-bottom: 1px solid #f0f2f5;
}

:deep(.el-card__body) {
  padding: 16px;
}

:deep(.el-table) {
  border-radius: 8px;

  th.el-table__cell {
    background: linear-gradient(180deg, #f5f7fa, #fafbfc);
    color: #303133;
    font-weight: 600;
  }
}

@media (max-width: 992px) {
  .summary-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 576px) {
  .summary-cards {
    grid-template-columns: 1fr;
  }
}
</style>
