<!-- CRM 客户分析报表 -->
<template>
  <!-- 搜索区域 -->
  <ContentWrap>
    <el-form ref="queryFormRef" :model="queryParams" label-width="auto">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="时间范围" prop="times">
            <el-date-picker
              v-model="queryParams.times"
              :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
              :shortcuts="defaultShortcuts"
              class="!w-240px"
              end-placeholder="结束时间"
              start-placeholder="开始时间"
              type="daterange"
              value-format="YYYY-MM-DD HH:mm:ss"
              @change="handleQuery"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="时间粒度" prop="interval">
            <el-select
              v-model="queryParams.interval"
              class="!w-240px"
              placeholder="请选择时间粒度"
              @change="handleQuery"
            >
              <el-option
                v-for="dict in getIntDictOptions(DICT_TYPE.DATE_INTERVAL)"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="部门" prop="deptId">
            <el-tree-select
              v-model="queryParams.deptId"
              :data="deptList"
              :props="defaultProps"
              check-strictly
              class="!w-240px"
              node-key="id"
              placeholder="请选择部门"
              :default-expanded-keys="[100, 101]"
              @change="(queryParams.userId = undefined), handleQuery()"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-form-item label="负责人" prop="userId">
            <el-select
              v-model="queryParams.userId"
              class="!w-240px"
              clearable
              placeholder="请选择负责人"
              @change="handleQuery"
            >
              <el-option
                v-for="(user, index) in userListByDeptId"
                :key="index"
                :label="user.nickname"
                :value="user.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item>
            <el-button @click="handleQuery">
              <Icon class="mr-5px" icon="ep:search" />
              查询
            </el-button>
            <el-button @click="resetQuery">
              <Icon class="mr-5px" icon="ep:refresh" />
              重置
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- 概览统计卡片 -->
  <el-row :gutter="16" class="mt-16px">
    <el-col :span="6">
      <el-card shadow="hover">
        <el-statistic title="客户总数" :value="overviewData.totalCustomers">
          <template #prefix>
            <Icon color="#409EFF" icon="ep:user-filled" />
          </template>
        </el-statistic>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="hover">
        <el-statistic title="本月新增" :value="overviewData.newThisMonth">
          <template #prefix>
            <Icon color="#67C23A" icon="ep:plus" />
          </template>
        </el-statistic>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="hover">
        <el-statistic title="成交客户" :value="overviewData.dealCustomers">
          <template #prefix>
            <Icon color="#E6A23C" icon="ep:circle-check-filled" />
          </template>
        </el-statistic>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="hover">
        <el-statistic title="成交率" :value="overviewData.dealRate + '%'">
          <template #prefix>
            <Icon color="#F56C6C" icon="ep:trend-charts" />
          </template>
        </el-statistic>
      </el-card>
    </el-col>
  </el-row>

  <!-- ECharts 图表区域 - 两两一行 -->
  <el-row :gutter="16" class="mt-16px">
    <!-- 客户来源分布 - 饼图 -->
    <el-col :span="12">
      <el-card shadow="never">
        <template #header>客户来源分布</template>
        <el-skeleton :loading="sourceLoading" animated>
          <Echart :height="400" :options="sourcePieOption" />
        </el-skeleton>
      </el-card>
    </el-col>

    <!-- 客户等级分布 - 柱状图 -->
    <el-col :span="12">
      <el-card shadow="never">
        <template #header>客户等级分布</template>
        <el-skeleton :loading="levelLoading" animated>
          <Echart :height="400" :options="levelBarOption" />
        </el-skeleton>
      </el-card>
    </el-col>
  </el-row>

  <el-row :gutter="16" class="mt-16px">
    <!-- 客户地区分布 - 横向柱状图 -->
    <el-col :span="12">
      <el-card shadow="never">
        <template #header>客户地区分布 Top10</template>
        <el-skeleton :loading="areaLoading" animated>
          <Echart :height="400" :options="areaBarOption" />
        </el-skeleton>
      </el-card>
    </el-col>

    <!-- 客户行业分布 - 饼图 -->
    <el-col :span="12">
      <el-card shadow="never">
        <template #header>客户行业分布</template>
        <el-skeleton :loading="industryLoading" animated>
          <Echart :height="400" :options="industryPieOption" />
        </el-skeleton>
      </el-card>
    </el-col>
  </el-row>

  <!-- 客户月度新增趋势 - 折线图 -->
  <el-row :gutter="16" class="mt-16px">
    <el-col :span="24">
      <el-card shadow="never">
        <template #header>客户月度新增与成交趋势</template>
        <el-skeleton :loading="trendLoading" animated>
          <Echart :height="400" :options="trendLineOption" />
        </el-skeleton>
      </el-card>
    </el-col>
  </el-row>

  <!-- 客户负责人统计表格 -->
  <el-row :gutter="16" class="mt-16px">
    <el-col :span="24">
      <el-card shadow="never">
        <template #header>客户负责人统计</template>
        <el-table v-loading="summaryLoading" :data="summaryByUserList" stripe>
          <el-table-column align="center" label="序号" type="index" width="60" />
          <el-table-column align="center" label="负责人" min-width="120" prop="ownerUserName" />
          <el-table-column align="right" label="客户数" min-width="120" prop="customerCreateCount" />
          <el-table-column align="right" label="成交客户数" min-width="120" prop="customerDealCount" />
          <el-table-column align="right" label="合同金额" min-width="150" prop="contractPrice">
            <template #default="scope">
              {{ formatPrice(scope.row.contractPrice) }}
            </template>
          </el-table-column>
          <el-table-column align="right" label="回款金额" min-width="150" prop="receivablePrice">
            <template #default="scope">
              {{ formatPrice(scope.row.receivablePrice) }}
            </template>
          </el-table-column>
          <el-table-column align="right" label="回款率" min-width="100">
            <template #default="scope">
              {{ erpCalculatePercentage(scope.row.receivablePrice, scope.row.contractPrice) }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import * as DeptApi from '@/api/system/dept'
import * as UserApi from '@/api/system/user'
import { useUserStore } from '@/store/modules/user'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { beginOfDay, defaultShortcuts, endOfDay, formatDate } from '@/utils/formatTime'
import { defaultProps, handleTree } from '@/utils/tree'
import { erpCalculatePercentage, erpPriceTableColumnFormatter } from '@/utils'
import {
  StatisticsCustomerApi,
  CrmStatisticsCustomerSummaryByDateRespVO,
  CrmStatisticsCustomerSummaryByUserRespVO
} from '@/api/crm/statistics/customer'
import { StatisticsPortraitApi } from '@/api/crm/statistics/portrait'
import { EChartsOption } from 'echarts'

defineOptions({ name: 'CrmCustomerAnalysisReport' })

// ========== 查询参数 ==========
const queryParams = reactive({
  interval: 2, // 默认按周
  deptId: 103,
  userId: undefined as number | undefined,
  times: [
    formatDate(beginOfDay(new Date(new Date().getTime() - 3600 * 1000 * 24 * 30))),
    formatDate(endOfDay(new Date()))
  ]
})

const queryFormRef = ref()
const deptList = ref<Tree[]>([])
const userList = ref<UserApi.UserVO[]>([])

const userListByDeptId = computed(() =>
  queryParams.deptId
    ? userList.value.filter((u: UserApi.UserVO) => u.deptId === queryParams.deptId)
    : []
)

// ========== 概览数据 ==========
const overviewData = reactive({
  totalCustomers: 0,
  newThisMonth: 0,
  dealCustomers: 0,
  dealRate: '0.00',
  followingCustomers: 0
})

// ========== 加载状态 ==========
const sourceLoading = ref(false)
const levelLoading = ref(false)
const areaLoading = ref(false)
const industryLoading = ref(false)
const trendLoading = ref(false)
const summaryLoading = ref(false)

// ========== 客户来源饼图 ==========
const sourcePieOption = reactive<EChartsOption>({
  tooltip: { trigger: 'item' },
  legend: { orient: 'vertical', right: 10, top: 'center' },
  series: [{
    type: 'pie',
    radius: ['40%', '70%'],
    center: ['40%', '50%'],
    avoidLabelOverlap: true,
    itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
    label: { show: true, formatter: '{b}: {c} ({d}%)' },
    data: []
  }]
}) as EChartsOption

// ========== 客户等级柱状图 ==========
const levelBarOption = reactive<EChartsOption>({
  tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
  xAxis: { type: 'category', data: [] },
  yAxis: { type: 'value', name: '客户数', minInterval: 1 },
  series: [{
    type: 'bar',
    name: '客户数',
    data: [],
    itemStyle: { borderRadius: [4, 4, 0, 0] }
  }]
}) as EChartsOption

// ========== 客户地区横向柱状图 ==========
const areaBarOption = reactive<EChartsOption>({
  tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
  grid: { left: '3%', right: '10%', bottom: '3%', containLabel: true },
  xAxis: { type: 'value', name: '客户数', minInterval: 1 },
  yAxis: { type: 'category', data: [], inverse: true },
  series: [{
    type: 'bar',
    name: '客户数',
    data: [],
    itemStyle: { borderRadius: [0, 4, 4, 0] }
  }]
}) as EChartsOption

// ========== 客户行业饼图 ==========
const industryPieOption = reactive<EChartsOption>({
  tooltip: { trigger: 'item' },
  legend: { orient: 'vertical', right: 10, top: 'center' },
  series: [{
    type: 'pie',
    radius: ['40%', '70%'],
    center: ['40%', '50%'],
    avoidLabelOverlap: true,
    itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
    label: { show: true, formatter: '{b}: {c} ({d}%)' },
    data: []
  }]
}) as EChartsOption

// ========== 月度趋势折线图 ==========
const trendLineOption = reactive<EChartsOption>({
  tooltip: { trigger: 'axis' },
  legend: { data: ['新增客户', '成交客户'] },
  xAxis: { type: 'category', data: [] },
  yAxis: { type: 'value', minInterval: 1 },
  series: [
    { type: 'line', name: '新增客户', data: [], smooth: true },
    { type: 'line', name: '成交客户', data: [], smooth: true }
  ]
}) as EChartsOption

// ========== 负责人统计表格 ==========
const summaryByUserList = ref<CrmStatisticsCustomerSummaryByUserRespVO[]>([])

/** 格式化金额 */
const formatPrice = (price: number | undefined | null) => {
  if (price == null || isNaN(price)) return '¥0.00'
  return `¥${Number(price).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

/** 加载概览数据 */
const loadOverview = async () => {
  try {
    const summaryByDate = await StatisticsCustomerApi.getCustomerSummaryByDate({
      ...queryParams,
      interval: 3 // 按月
    })
    let total = 0, deals = 0, news = 0
    summaryByDate.forEach((vo: CrmStatisticsCustomerSummaryByDateRespVO) => {
      total += vo.customerCreateCount
      deals += vo.customerDealCount
    })
    // 本月新增：取最近一个月的数据
    if (summaryByDate.length > 0) {
      news = summaryByDate[summaryByDate.length - 1].customerCreateCount
    }
    overviewData.totalCustomers = total
    overviewData.newThisMonth = news
    overviewData.dealCustomers = deals
    overviewData.dealRate = total > 0 ? ((deals / total) * 100).toFixed(2) : '0.00'
  } catch (e) {/* ignore */}
}

/** 加载来源分布 */
const loadSourceData = async () => {
  sourceLoading.value = true
  try {
    const data = await StatisticsPortraitApi.getCustomerSource(queryParams)
    const pieData = data.map((vo: any) => ({ name: vo.sourceName || `来源${vo.source}`, value: vo.customerCount }))
    ;(sourcePieOption.series![0] as any).data = pieData
  } finally {
    sourceLoading.value = false
  }
}

/** 加载等级分布 */
const loadLevelData = async () => {
  levelLoading.value = true
  try {
    const data = await StatisticsPortraitApi.getCustomerLevel(queryParams)
    ;(levelBarOption.xAxis as any).data = data.map((vo: any) => vo.levelName || `等级${vo.level}`)
    ;(levelBarOption.series![0] as any).data = data.map((vo: any) => vo.customerCount)
  } finally {
    levelLoading.value = false
  }
}

/** 加载地区分布 */
const loadAreaData = async () => {
  areaLoading.value = true
  try {
    const data = await StatisticsPortraitApi.getCustomerArea(queryParams)
    const sorted = [...data].sort((a: any, b: any) => b.customerCount - a.customerCount).slice(0, 10)
    ;(areaBarOption.yAxis as any).data = sorted.map((vo: any) => vo.areaName || '未知')
    ;(areaBarOption.series![0] as any).data = sorted.map((vo: any) => vo.customerCount)
  } finally {
    areaLoading.value = false
  }
}

/** 加载行业分布 */
const loadIndustryData = async () => {
  industryLoading.value = true
  try {
    const data = await StatisticsPortraitApi.getCustomerIndustry(queryParams)
    const pieData = data.map((vo: any) => ({ name: vo.industryName || `行业${vo.industryId}`, value: vo.customerCount }))
    ;(industryPieOption.series![0] as any).data = pieData
  } finally {
    industryLoading.value = false
  }
}

/** 加载月度趋势 */
const loadTrendData = async () => {
  trendLoading.value = true
  try {
    const data = await StatisticsCustomerApi.getCustomerSummaryByDate(queryParams)
    ;(trendLineOption.xAxis as any).data = data.map((vo: CrmStatisticsCustomerSummaryByDateRespVO) => vo.time)
    ;(trendLineOption.series![0] as any).data = data.map((vo: CrmStatisticsCustomerSummaryByDateRespVO) => vo.customerCreateCount)
    ;(trendLineOption.series![1] as any).data = data.map((vo: CrmStatisticsCustomerSummaryByDateRespVO) => vo.customerDealCount)
  } finally {
    trendLoading.value = false
  }
}

/** 加载负责人统计 */
const loadSummaryByUser = async () => {
  summaryLoading.value = true
  try {
    const data = await StatisticsCustomerApi.getCustomerSummaryByUser(queryParams)
    summaryByUserList.value = data
  } finally {
    summaryLoading.value = false
  }
}

/** 查询 */
const handleQuery = async () => {
  await Promise.all([
    loadOverview(),
    loadSourceData(),
    loadLevelData(),
    loadAreaData(),
    loadIndustryData(),
    loadTrendData(),
    loadSummaryByUser()
  ])
}

/** 重置 */
const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}

/** 初始化 */
onMounted(async () => {
  deptList.value = handleTree(await DeptApi.getSimpleDeptList())
  userList.value = handleTree(await UserApi.getSimpleUserList())
  await handleQuery()
})
</script>
