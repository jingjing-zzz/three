<template>
  <div class="forecast-page">
    <!-- 顶部筛选区 -->
    <el-card class="filter-card" shadow="never">
      <div class="filter-header">
        <div class="filter-title">
          <Icon icon="ep:data-analysis" :size="20" class="title-icon" />
          <span>销售预测分析</span>
        </div>
        <div class="quick-range">
          <el-button
            v-for="opt in quickRanges"
            :key="opt.key"
            :type="queryParams.activeRange === opt.key ? 'primary' : 'default'"
            size="small"
            round
            @click="applyQuickRange(opt.key)"
          >
            {{ opt.label }}
          </el-button>
        </div>
      </div>
      <el-divider class="filter-divider" />
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
          <div class="card-label">活跃商机</div>
          <div class="card-value">{{ summary.businessCount || 0 }}</div>
          <div class="card-sub">
            <Icon icon="ep:circle-check" :size="12" class="sub-icon" />
            已成交 {{ summary.wonCount || 0 }} 单
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
          <div class="card-value">¥{{ formatMoney(summary.totalAmount) }}</div>
          <div class="card-sub">
            <Icon icon="ep:wallet" :size="12" class="sub-icon" />
            已成交 ¥{{ formatMoney(summary.wonAmount) }}
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
          <div class="card-value">¥{{ formatMoney(summary.forecastAmount) }}</div>
          <div class="card-sub">
            <Icon icon="ep:top" :size="12" class="sub-icon" />
            潜力 ¥{{ formatMoney((summary.forecastAmount || 0) - (summary.wonAmount || 0)) }}
          </div>
        </div>
      </div>
      <div class="summary-card gradient-purple">
        <div class="card-decorator"></div>
        <div class="card-icon-wrap">
          <Icon icon="ep:data-line" :size="24" />
        </div>
        <div class="card-body">
          <div class="card-label">同比变化</div>
          <div class="card-value" :class="{ 'negative': (summary.growthRate || 0) < 0, 'positive': (summary.growthRate || 0) > 0 }">
            <Icon
              v-if="summary.growthRate != null"
              :icon="summary.growthRate > 0 ? 'ep:caret-top' : (summary.growthRate < 0 ? 'ep:caret-bottom' : 'ep:minus')"
              :size="18"
              class="trend-icon"
            />
            {{ summary.growthRate != null ? Math.abs(summary.growthRate).toFixed(2) + '%' : '--' }}
          </div>
          <div class="card-sub">
            <Icon icon="ep:clock" :size="12" class="sub-icon" />
            去年同期 ¥{{ formatMoney(summary.historyAmount) }}
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区 -->
    <el-card class="chart-card" shadow="never">
      <template #header>
        <div class="chart-header">
          <div class="chart-title-wrap">
            <span class="chart-title-bar"></span>
            <span class="chart-title">
              <Icon icon="ep:pie-chart" class="mr-5px" />
              销售预测趋势（按日）
            </span>
          </div>
          <div class="chart-actions">
            <el-radio-group v-model="chartMode" size="small" @change="updateChart">
              <el-radio-button label="compare">对比</el-radio-button>
              <el-radio-button label="business">商机金额</el-radio-button>
              <el-radio-button label="forecast">预测金额</el-radio-button>
              <el-radio-button label="gap">预测差异</el-radio-button>
            </el-radio-group>
            <el-tooltip content="点击柱子可筛选当日商机" placement="top">
              <el-tag size="small" type="info" effect="plain" round class="ml-10px">
                <Icon icon="ep:info-filled" :size="12" class="mr-2px" />
                交互提示
              </el-tag>
            </el-tooltip>
          </div>
        </div>
      </template>
      <Echart v-if="chartOption" :options="chartOption" :height="500" @click="handleChartClick" />
    </el-card>

    <!-- 明细表 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="table-header">
          <div class="chart-title-wrap">
            <span class="chart-title-bar bar-green"></span>
            <span class="table-title">
              <Icon icon="ep:list" class="mr-5px" />
              商机预测明细
            </span>
          </div>
          <el-tag v-if="clickedDate" type="warning" closable effect="light" round @close="clearDateFilter">
            <Icon icon="ep:filter" :size="12" class="mr-2px" />
            已筛选：{{ clickedDate }}
          </el-tag>
        </div>
      </template>
      <el-table v-loading="loading" :data="list" border stripe :row-class-name="rowClassName">
        <el-table-column label="商机名称" prop="businessName" min-width="160">
          <template #default="scope">
            <el-link type="primary" :underline="false" @click="openBusinessDetail(scope.row.businessId)">
              {{ scope.row.businessName }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="负责人" prop="ownerUserName" min-width="100" />
        <el-table-column label="客户名称" prop="customerName" min-width="120" />
        <el-table-column label="当前阶段" prop="statusName" min-width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.statusName" type="info" effect="light" round>{{ scope.row.statusName }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="阶段概率" prop="stagePercent" width="120" align="center">
          <template #default="scope">
            <el-progress
              v-if="scope.row.stagePercent != null"
              :percentage="scope.row.stagePercent"
              :stroke-width="8"
              :show-text="true"
              :color="getProgressColor(scope.row.stagePercent)"
            />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="商机金额" prop="totalPrice" width="140" align="right">
          <template #default="scope">¥{{ formatMoney(scope.row.totalPrice) }}</template>
        </el-table-column>
        <el-table-column label="预测金额" prop="forecastAmount" width="140" align="right">
          <template #default="scope">
            <span class="forecast-amount">¥{{ formatMoney(scope.row.forecastAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="预计成交时间" prop="dealTime" min-width="170" align="center" />
        <el-table-column label="状态" prop="endStatus" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.endStatus)" effect="light" round>
              {{ getStatusText(scope.row.endStatus) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNo"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import * as UserApi from '@/api/system/user'
import { beginOfDay, endOfDay, formatDate } from '@/utils/formatTime'
import { ForecastApi, ForecastByDateVO } from '@/api/crm/statistics/forecast'
import Echart from '@/components/Echart/src/Echart.vue'
import Pagination from '@/components/Pagination/index.vue'

defineOptions({ name: 'CrmStatisticsForecast' })

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const chartOption = ref<any>(null)
const chartMode = ref<'compare' | 'business' | 'forecast' | 'gap'>('compare')
const clickedDate = ref<string>('')
const timeRangeMap = new Map<string, [string, string]>()
const forecastByDateList = ref<ForecastByDateVO[]>([])

const queryParams = reactive({
  pageNo: 1,
  pageSize: 20,
  userId: undefined as number | undefined,
  activeRange: 'near30to90',
  times: [
    formatDate(beginOfDay(new Date(new Date().getTime() - 3600 * 1000 * 24 * 30))),
    formatDate(endOfDay(new Date(new Date().getTime() + 3600 * 1000 * 24 * 90)))
  ]
})

const userList = ref<any[]>([])

const summary = reactive({
  businessCount: 0,
  totalAmount: 0,
  forecastAmount: 0,
  wonAmount: 0,
  wonCount: 0,
  avgWinRate: 0,
  historyAmount: 0,
  growthRate: 0,
  achievementRate: null as number | null
})

const quickRanges = [
  { key: 'future30', label: '未来30天', days: [0, 30] },
  { key: 'future90', label: '未来90天', days: [0, 90] },
  { key: 'near30to90', label: '近30到未来90天', days: [-30, 90] },
  { key: 'near30to180', label: '近30到未来180天', days: [-30, 180] }
]

const dateShortcuts = computed(() => [
  {
    text: '未来30天',
    value: () => {
      const end = new Date()
      end.setTime(end.getTime() + 3600 * 1000 * 24 * 30)
      return [new Date(), end]
    }
  },
  {
    text: '未来90天',
    value: () => {
      const end = new Date()
      end.setTime(end.getTime() + 3600 * 1000 * 24 * 90)
      return [new Date(), end]
    }
  },
  {
    text: '近30天到未来180天',
    value: () => {
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
      const end = new Date()
      end.setTime(end.getTime() + 3600 * 1000 * 24 * 180)
      return [start, end]
    }
  }
])

const formatMoney = (value: number) => {
  if (value == null || isNaN(Number(value))) return '0.00'
  return Number(value).toFixed(2)
}

const getStatusTagType = (endStatus: number) => {
  if (endStatus === 1) return 'success'
  if (endStatus === 2) return 'danger'
  return 'warning'
}

const getStatusText = (endStatus: number) => {
  if (endStatus === 1) return '已赢单'
  if (endStatus === 2) return '已输单'
  return '进行中'
}

const getProgressColor = (percent: number) => {
  if (percent < 30) return '#F56C6C'
  if (percent < 60) return '#E6A23C'
  if (percent < 90) return '#409EFF'
  return '#67C23A'
}

const rowClassName = ({ row }: { row: any }) => {
  if (row.endStatus === 1) return 'row-won'
  if (row.endStatus === 2) return 'row-lost'
  return ''
}

const applyQuickRange = (key: string) => {
  const range = quickRanges.find(r => r.key === key)
  if (!range) return
  queryParams.activeRange = key
  const start = new Date()
  start.setTime(start.getTime() + 3600 * 1000 * 24 * range.days[0])
  const end = new Date()
  end.setTime(end.getTime() + 3600 * 1000 * 24 * range.days[1])
  queryParams.times = [formatDate(beginOfDay(start)), formatDate(endOfDay(end))]
  handleQuery()
}

const buildForecastParams = (includePage = true) => {
  const filters: any = {}
  if (queryParams.userId) {
    filters.userIds = [queryParams.userId]
  }
  if (queryParams.times && queryParams.times.length === 2) {
    filters.dealTimeStart = queryParams.times[0]
    filters.dealTimeEnd = queryParams.times[1]
  }
  if (includePage) {
    filters.pageNo = queryParams.pageNo
    filters.pageSize = Math.min(queryParams.pageSize || 20, 200)
  }
  return filters
}

const getList = async () => {
  loading.value = true
  try {
    const data = await ForecastApi.getForecastPage(buildForecastParams())
    list.value = data.list || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

const getSummary = async () => {
  try {
    const data = await ForecastApi.getForecastSummary(buildForecastParams(false))
    Object.assign(summary, data)
  } catch (error) {
    console.error('获取销售预测汇总失败', error)
  }
}

const getChartData = async () => {
  try {
    const data = await ForecastApi.getForecastByDate(buildForecastParams(false))
    forecastByDateList.value = data || []
    updateChart()
  } catch (error) {
    console.error('获取预测图表数据失败', error)
    forecastByDateList.value = []
    updateChart()
  }
}

const updateChart = () => {
  if (!forecastByDateList.value || forecastByDateList.value.length === 0) {
    chartOption.value = {
      title: { text: '暂无数据', left: 'center', top: 'center', textStyle: { color: '#999', fontSize: 16 } }
    }
    return
  }

  // 构建 timeRangeMap（用于点击筛选）
  timeRangeMap.clear()
  forecastByDateList.value.forEach((item) => {
    if (item.startTime && item.endTime) {
      timeRangeMap.set(item.time, [item.startTime, item.endTime])
    }
  })

  const dates = forecastByDateList.value.map(d => d.time)
  const businessAmounts = forecastByDateList.value.map(d => Number(d.businessAmount) || 0)
  const forecastAmounts = forecastByDateList.value.map(d => Number(d.forecastAmount) || 0)
  const historyAmounts = forecastByDateList.value.map(d => Number(d.historyAmount) || 0)

  // 根据 chartMode 选择性构建 series
  const series: any[] = []
  if (chartMode.value === 'compare' || chartMode.value === 'business') {
    series.push({
      name: '商机金额',
      type: 'bar',
      data: businessAmounts,
      barMaxWidth: 22,
      itemStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: '#409EFF' },
            { offset: 1, color: '#a0cfff' }
          ]
        },
        borderRadius: [6, 6, 0, 0]
      },
      emphasis: {
        itemStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: '#337ecc' },
              { offset: 1, color: '#79bbff' }
            ]
          }
        }
      }
    })
  }
  if (chartMode.value === 'compare' || chartMode.value === 'forecast') {
    series.push({
      name: '预测金额',
      type: 'bar',
      data: forecastAmounts,
      barMaxWidth: 22,
      itemStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: '#67C23A' },
            { offset: 1, color: '#b3e19d' }
          ]
        },
        borderRadius: [6, 6, 0, 0]
      },
      emphasis: {
        itemStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: '#529b2e' },
              { offset: 1, color: '#95d475' }
            ]
          }
        }
      }
    })
  }
  if (chartMode.value === 'compare') {
    series.push({
      name: '历史同期成交',
      type: 'line',
      data: historyAmounts,
      smooth: true,
      symbol: 'circle',
      symbolSize: 7,
      lineStyle: { width: 2.5, color: '#F56C6C', type: 'dashed', shadowColor: 'rgba(245,108,108,0.3)', shadowBlur: 6 },
      itemStyle: { color: '#F56C6C', borderColor: '#fff', borderWidth: 2 },
      emphasis: { focus: 'series', lineStyle: { width: 3 } }
    })
  }
  if (chartMode.value === 'gap') {
    // 预测差异：预测金额 - 商机金额（负值表示预测低于商机金额，正值表示高于）
    const gapAmounts = dates.map((_, i) => Number((Number(forecastAmounts[i]) - Number(businessAmounts[i])).toFixed(2)))
    series.push({
      name: '预测差异',
      type: 'bar',
      data: gapAmounts,
      barMaxWidth: 22,
      itemStyle: {
        color: (params: any) => {
          return params.value >= 0 ? '#67C23A' : '#F56C6C'
        },
        borderRadius: [4, 4, 0, 0]
      },
      markLine: {
        silent: true,
        data: [{ yAxis: 0 }],
        lineStyle: { color: '#909399', type: 'dashed', width: 1 }
      }
    })
  }

  // 根据 chartMode 构建 legend
  const legendData: string[] = []
  if (chartMode.value === 'compare') legendData.push('商机金额', '预测金额', '历史同期成交')
  else if (chartMode.value === 'business') legendData.push('商机金额')
  else if (chartMode.value === 'forecast') legendData.push('预测金额')
  else if (chartMode.value === 'gap') legendData.push('预测差异')

  // 根据 chartMode 构建 yAxis 名称
  const yAxisName = chartMode.value === 'business' ? '商机金额 (¥)'
    : chartMode.value === 'forecast' ? '预测金额 (¥)'
    : chartMode.value === 'gap' ? '预测 - 商机 (¥)'
    : '金额 (¥)'

  chartOption.value = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: 'rgba(255, 255, 255, 0.98)',
      borderColor: '#ebeef5',
      borderWidth: 1,
      textStyle: { color: '#303133', fontSize: 12 },
      extraCssText: 'box-shadow: 0 4px 16px rgba(0,0,0,0.08); border-radius: 8px; padding: 10px 14px;',
      formatter: (params: any) => {
        let result = `<div style="font-weight:600;margin-bottom:6px;color:#303133;">${params[0].name}</div>`
        params.forEach((p: any) => {
          const val = Number(p.value).toFixed(2)
          result += `<div style="display:flex;justify-content:space-between;align-items:center;margin:3px 0;min-width:160px;">
            <span>${p.marker} ${p.seriesName}</span>
            <span style="font-weight:600;color:${p.color};">¥${val}</span>
          </div>`
        })
        return result
      }
    },
    legend: {
      data: legendData,
      top: 10,
      right: 20,
      textStyle: { fontSize: 12, color: '#606266' },
      itemWidth: 14,
      itemHeight: 14,
      itemGap: 20
    },
    grid: {
      top: 60,
      left: 20,
      right: 30,
      bottom: 90,
      containLabel: true
    },
    dataZoom: [
      {
        type: 'slider',
        show: true,
        height: 22,
        bottom: 25,
        start: 0,
        end: Math.min(100, (30 / dates.length) * 100),
        handleSize: '120%',
        borderColor: '#dcdfe6',
        fillerColor: 'rgba(64, 158, 255, 0.15)',
        handleStyle: { color: '#409EFF', borderColor: '#409EFF' },
        textStyle: { color: '#909399', fontSize: 10 }
      },
      { type: 'inside' }
    ],
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45,
        interval: 0,
        fontSize: 10,
        color: '#606266',
        formatter: (val: string) => val ? val.substring(5) : val
      },
      axisLine: { lineStyle: { color: '#dcdfe6' } },
      axisTick: { alignWithLabel: true }
    },
    yAxis: {
      type: 'value',
      name: yAxisName,
      nameTextStyle: { color: '#909399', fontSize: 11 },
      axisLabel: {
        color: '#606266',
        formatter: (val: number) => {
          if (val >= 10000) return (val / 10000).toFixed(1) + '万'
          return val.toFixed(0)
        }
      },
      splitLine: { lineStyle: { type: 'dashed', color: '#f0f0f0' } },
      axisLine: { show: false },
      axisTick: { show: false }
    },
    series: series,
  }
}

const handleChartClick = (params: any) => {
  const date = typeof params.name === 'string' ? params.name : undefined
  const range = date ? timeRangeMap.get(date) : undefined
  if (!range) return
  clickedDate.value = date
  queryParams.pageNo = 1
  queryParams.times = [...range]
  getList()
}

const clearDateFilter = () => {
  clickedDate.value = ''
  queryParams.times = [
    formatDate(beginOfDay(new Date(new Date().getTime() - 3600 * 1000 * 24 * 30))),
    formatDate(endOfDay(new Date(new Date().getTime() + 3600 * 1000 * 24 * 90)))
  ]
  queryParams.pageNo = 1
  getList()
}

const handleQuery = () => {
  clickedDate.value = ''
  queryParams.pageNo = 1
  getList()
  getSummary()
  getChartData()
}

const resetQuery = () => {
  Object.assign(queryParams, {
    pageNo: 1,
    pageSize: 20,
    userId: undefined,
    activeRange: 'near30to90',
    times: [
      formatDate(beginOfDay(new Date(new Date().getTime() - 3600 * 1000 * 24 * 30))),
      formatDate(endOfDay(new Date(new Date().getTime() + 3600 * 1000 * 24 * 90)))
    ]
  })
  clickedDate.value = ''
  handleQuery()
}

const initOptions = async () => {
  userList.value = await UserApi.getSimpleUserList()
}

const { push } = useRouter()
const openBusinessDetail = (id: number) => {
  push({ name: 'CrmBusinessDetail', params: { id } })
}

onMounted(() => {
  initOptions()
  getList()
  getSummary()
  getChartData()
})
</script>

<style lang="scss" scoped>
.forecast-page {
  padding: 20px;
  background: linear-gradient(180deg, #f5f7fa 0%, #fafbfc 100%);
  min-height: calc(100vh - 100px);
}

/* ============ 筛选区 ============ */
.filter-card {
  margin-bottom: 20px;
  border-radius: 10px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  :deep(.el-card__body) { padding: 18px 20px 2px; }
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  .filter-title {
    display: inline-flex;
    align-items: center;
    font-size: 17px;
    font-weight: 600;
    color: #303133;
    .title-icon {
      margin-right: 8px;
      color: #409EFF;
    }
  }
  .quick-range {
    display: inline-flex;
    gap: 6px;
    flex-wrap: wrap;
  }
}

.filter-divider {
  margin: 12px 0 16px;
  border-color: #f0f2f5;
}

/* ============ 汇总卡片 ============ */
.summary-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.summary-card {
  position: relative;
  display: flex;
  align-items: center;
  padding: 22px 20px;
  border-radius: 12px;
  color: #fff;
  overflow: hidden;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.08);
  transition: transform 0.25s cubic-bezier(0.4, 0, 0.2, 1), box-shadow 0.25s ease;
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 22px rgba(0, 0, 0, 0.14);
  }
  .card-decorator {
    position: absolute;
    right: -20px;
    top: -20px;
    width: 110px;
    height: 110px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.12);
    pointer-events: none;
    &::before {
      content: '';
      position: absolute;
      right: -15px;
      bottom: -15px;
      width: 70px;
      height: 70px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.08);
    }
  }
  .card-icon-wrap {
    position: relative;
    z-index: 1;
    width: 48px;
    height: 48px;
    border-radius: 12px;
    background: rgba(255, 255, 255, 0.22);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16px;
    flex-shrink: 0;
  }
  .card-body {
    position: relative;
    z-index: 1;
    flex: 1;
    min-width: 0;
  }
  .card-label {
    font-size: 13px;
    opacity: 0.9;
    margin-bottom: 6px;
    letter-spacing: 0.5px;
  }
  .card-value {
    font-size: 26px;
    font-weight: 700;
    line-height: 1.2;
    display: inline-flex;
    align-items: center;
    gap: 4px;
    .trend-icon { font-size: 18px; }
    &.positive { color: #fff; }
    &.negative { color: #ffe5e5; }
  }
  .card-sub {
    font-size: 12px;
    opacity: 0.85;
    margin-top: 6px;
    display: inline-flex;
    align-items: center;
    gap: 4px;
    .sub-icon { opacity: 0.7; }
  }
}

.gradient-blue {
  background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
}
.gradient-green {
  background: linear-gradient(135deg, #52c41a 0%, #95de64 100%);
}
.gradient-orange {
  background: linear-gradient(135deg, #fa8c16 0%, #ffc53d 100%);
}
.gradient-purple {
  background: linear-gradient(135deg, #722ed1 0%, #b37feb 100%);
}

/* ============ 图表区 ============ */
.chart-card {
  margin-bottom: 20px;
  border-radius: 10px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  :deep(.el-card__header) {
    padding: 14px 20px;
    border-bottom: 1px solid #f0f2f5;
  }
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  .chart-title-wrap {
    display: inline-flex;
    align-items: center;
  }
  .chart-title-bar {
    display: inline-block;
    width: 4px;
    height: 16px;
    background: linear-gradient(180deg, #409EFF, #36cfc9);
    border-radius: 2px;
    margin-right: 10px;
    &.bar-green {
      background: linear-gradient(180deg, #67C23A, #95de64);
    }
  }
  .chart-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    display: inline-flex;
    align-items: center;
  }
}

/* ============ 表格区 ============ */
.table-card {
  border-radius: 10px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  :deep(.el-card__header) {
    padding: 14px 20px;
    border-bottom: 1px solid #f0f2f5;
  }
  :deep(.el-table) {
    border-radius: 6px;
    overflow: hidden;
    th.el-table__cell {
      background: #fafbfc;
      color: #303133;
      font-weight: 600;
    }
    .row-won {
      background-color: rgba(103, 194, 58, 0.04) !important;
    }
    .row-lost {
      background-color: rgba(245, 108, 108, 0.04) !important;
    }
    tbody tr:hover > td {
      background-color: rgba(64, 158, 255, 0.05) !important;
    }
  }
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  .chart-title-wrap {
    display: inline-flex;
    align-items: center;
  }
  .table-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    display: inline-flex;
    align-items: center;
  }
}

.forecast-amount {
  color: #67c23a;
  font-weight: 600;
}
</style>
