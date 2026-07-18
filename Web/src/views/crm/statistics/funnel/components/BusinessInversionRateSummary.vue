<!-- 客户总量统计 -->
<template>
  <!-- Echarts�?-->
  <el-card shadow="never">
    <el-skeleton :loading="loading" animated>
      <Echart :height="500" :options="echartsOption" @click="handleChartClick" />
    </el-skeleton>
  </el-card>

  <!-- 统计列表 -->
  <el-card class="mt-16px" shadow="never">
    <el-table v-loading="loading" :data="list" :table-layout="'auto'">
      <el-table-column align="center" fixed="left" :label="t('customer.index')" type="index" width="80" />
      <el-table-column align="center" fixed="left" :label="t('funnel.businessName')" prop="name" min-width="160">
        <template #default="scope">
          <el-link :underline="false" type="primary" @click="openDetail(scope.row.id)">
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="left" :label="t('funnel.customerName')" prop="customerName" min-width="120">
        <template #default="scope">
          <el-link
            :underline="false"
            type="primary"
            @click="openCustomerDetail(scope.row.customerId)"
          >
            {{ scope.row.customerName }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column
        :formatter="erpPriceTableColumnFormatter"
        align="center"
        :label="t('funnel.totalPrice')"
        prop="totalPrice"
        min-width="140"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('funnel.dealTime')"
        prop="dealTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('funnel.remark')" prop="remark" min-width="200" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('funnel.contactNextTime')"
        prop="contactNextTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('funnel.ownerUserName')" prop="ownerUserName" min-width="100" />
      <el-table-column align="center" :label="t('funnel.ownerUserDeptName')" prop="ownerUserDeptName" min-width="100" />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('funnel.contactLastTime')"
        prop="contactLastTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('funnel.updateTime')"
        prop="updateTime"
        min-width="180"
      />
      <el-table-column
        :formatter="dateFormatter"
        align="center"
        :label="t('funnel.createTime')"
        prop="createTime"
        min-width="180"
      />
      <el-table-column align="center" :label="t('funnel.creatorName')" prop="creatorName" min-width="100" />
      <el-table-column
        align="center"
        fixed="right"
        :label="t('funnel.statusTypeName')"
        prop="statusTypeName"
        min-width="140"
      />
      <el-table-column
        align="center"
        fixed="right"
        :label="t('funnel.statusName')"
        prop="statusName"
        min-width="120"
      />
    </el-table>
    <!-- 分页 -->
    <Pagination
      v-model:limit="queryParams0.pageSize"
      v-model:page="queryParams0.pageNo"
      :total="total"
      @pagination="getList"
    />
  </el-card>
</template>
<script lang="ts" setup>
import {
  CrmStatisticsBusinessInversionRateSummaryByDateRespVO,
  StatisticFunnelApi
} from '@/api/crm/statistics/funnel'
import { EChartsOption } from 'echarts'
import { erpCalculatePercentage, erpPriceTableColumnFormatter } from '@/utils'
import { dateFormatter } from '@/utils/formatTime'

defineOptions({ name: 'BusinessSummary' })

const { t } = useI18n('crm.statistics') // 国际�?
const props = defineProps<{ queryParams: any }>() // 搜索参数
const queryParams0 = reactive<{
  pageNo: number
  pageSize: number
  times?: string[]
}>({
  pageNo: 1,
  pageSize: 10
})
const loading = ref(false) // 加载中
const list = ref([]) // 列表数据
const total = ref(0)
const timeRangeMap = new Map<string, [string, string]>()
/** 将传进来的值赋值给 queryParams0 */
watch(
  () => props.queryParams,
  (data) => {
    if (!data) {
      return
    }
    const newObj = { ...queryParams0, ...data }
    Object.assign(queryParams0, newObj)
  },
  {
    immediate: true
  }
)
const formatDateTime = (date: Date) => {
  const pad = (value: number) => String(value).padStart(2, '0')
  return date.getFullYear() + '-' + pad(date.getMonth() + 1) + '-' + pad(date.getDate())
    + ' ' + pad(date.getHours()) + ':' + pad(date.getMinutes()) + ':' + pad(date.getSeconds())
}

/** 后端未升级时，按当前筛选周期计算图表统计桶，保证点击筛选仍可使用。 */
const buildTimeRanges = (): Array<[string, string]> => {
  const times = props.queryParams?.times
  if (!Array.isArray(times) || times.length !== 2) return []
  let start = new Date(String(times[0]).replace(' ', 'T'))
  const end = new Date(String(times[1]).replace(' ', 'T'))
  if (Number.isNaN(start.getTime()) || Number.isNaN(end.getTime())) return []
  start.setHours(0, 0, 0, 0)
  end.setHours(23, 59, 59, 0)
  const ranges: Array<[string, string]> = []
  while (start <= end) {
    const rangeStart = new Date(start)
    const rangeEnd = new Date(start)
    switch (props.queryParams.interval) {
      case 2:
        rangeEnd.setDate(rangeEnd.getDate() + (7 - rangeEnd.getDay()))
        rangeEnd.setHours(23, 59, 59, 0)
        break
      case 3:
        rangeEnd.setMonth(rangeEnd.getMonth() + 1, 0)
        rangeEnd.setHours(23, 59, 59, 0)
        break
      case 4:
        rangeEnd.setMonth((Math.floor(rangeEnd.getMonth() / 3) + 1) * 3, 0)
        rangeEnd.setHours(23, 59, 59, 0)
        break
      case 5:
        rangeEnd.setMonth(11, 31)
        rangeEnd.setHours(23, 59, 59, 0)
        break
      default:
        rangeEnd.setHours(23, 59, 59, 0)
    }
    if (rangeEnd > end) rangeEnd.setTime(end.getTime())
    ranges.push([formatDateTime(rangeStart), formatDateTime(rangeEnd)])
    start = new Date(rangeEnd.getTime() + 1000)
  }
  return ranges
}

/** 柱状图配置：纵向 */
const echartsOption = reactive<EChartsOption>({
  color: ['#6ca2ff', '#6ac9d7', '#ff7474'],
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      // 坐标轴指示器，坐标轴触发有效
      type: 'shadow' // 默认为直线，可选为�?line' | 'shadow'
    }
  },
  legend: {
    data: [t('funnel.winConversionRate'), t('funnel.totalBusinessCount'), t('funnel.winBusinessCount')],
    bottom: '0px',
    itemWidth: 14
  },
  grid: {
    top: '40px',
    left: '40px',
    right: '40px',
    bottom: '40px',
    containLabel: true,
    borderColor: '#fff'
  },
  xAxis: [
    {
      type: 'category',
      data: [],
      axisTick: {
        alignWithLabel: true,
        lineStyle: { width: 0 }
      },
      axisLabel: {
        color: '#BDBDBD'
      },
      /** 坐标轴轴线相关设�?*/
      axisLine: {
        lineStyle: { color: '#BDBDBD' }
      },
      splitLine: {
        show: false
      }
    }
  ],
  yAxis: [
    {
      type: 'value',
      name: t('funnel.winConversionRate'),
      axisTick: {
        alignWithLabel: true,
        lineStyle: { width: 0 }
      },
      axisLabel: {
        color: '#BDBDBD',
        formatter: '{value}%'
      },
      /** 坐标轴轴线相关设�?*/
      axisLine: {
        lineStyle: { color: '#BDBDBD' }
      },
      splitLine: {
        show: false
      }
    },
    {
      type: 'value',
      name: t('funnel.businessCount'),
      axisTick: {
        alignWithLabel: true,
        lineStyle: { width: 0 }
      },
      axisLabel: {
        color: '#BDBDBD',
        formatter: `{value}${t('funnel.businessUnit')}`
      },
      /** 坐标轴轴线相关设�?*/
      axisLine: {
        lineStyle: { color: '#BDBDBD' }
      },
      splitLine: {
        show: false
      }
    }
  ],
  series: [
    {
      name: t('funnel.winConversionRate'),
      type: 'line',
      yAxisIndex: 0,
      data: []
    },
    {
      name: t('funnel.totalBusinessCount'),
      type: 'bar',
      yAxisIndex: 1,
      barWidth: 15,
      data: []
    },
    {
      name: t('funnel.winBusinessCount'),
      type: 'bar',
      yAxisIndex: 1,
      barWidth: 15,
      data: []
    }
  ]
}) as EChartsOption

/** 获取数据并填充图�?*/
const fetchAndFill = async () => {
  // 1. 加载统计数据
  const businessSummaryByDate = await StatisticFunnelApi.getBusinessInversionRateSummaryByDate(
    props.queryParams
  )
  // 2.1 更新 Echarts 数据
  timeRangeMap.clear()
  const fallbackRanges = buildTimeRanges()
  businessSummaryByDate.forEach((summary: CrmStatisticsBusinessInversionRateSummaryByDateRespVO, index: number) => {
    const timeRange = summary.startTime && summary.endTime
      ? [summary.startTime, summary.endTime] as [string, string]
      : fallbackRanges[index]
    if (timeRange) timeRangeMap.set(summary.time, timeRange)
  })

  if (echartsOption.xAxis && echartsOption.xAxis[0] && echartsOption.xAxis[0]['data']) {
    echartsOption.xAxis[0]['data'] = businessSummaryByDate.map(
      (s: CrmStatisticsBusinessInversionRateSummaryByDateRespVO) => s.time
    )
  }
  if (echartsOption.series && echartsOption.series[0] && echartsOption.series[0]['data']) {
    echartsOption.series[0]['data'] = businessSummaryByDate.map(
      (s: CrmStatisticsBusinessInversionRateSummaryByDateRespVO) =>
        erpCalculatePercentage(s.businessWinCount, s.businessCount)
    )
  }
  if (echartsOption.series && echartsOption.series[1] && echartsOption.series[1]['data']) {
    echartsOption.series[1]['data'] = businessSummaryByDate.map(
      (s: CrmStatisticsBusinessInversionRateSummaryByDateRespVO) => s.businessCount
    )
  }
  if (echartsOption.series && echartsOption.series[2] && echartsOption.series[2]['data']) {
    echartsOption.series[2]['data'] = businessSummaryByDate.map(
      (s: CrmStatisticsBusinessInversionRateSummaryByDateRespVO) => s.businessWinCount
    )
  }

  // 2.2 更新列表数据
  await getList()
}
/** 获取商机列表 */
const getList = async () => {
  const data = await StatisticFunnelApi.getBusinessPageByDate({ ...props.queryParams, ...queryParams0 })
  list.value = data.list
  total.value = data.total
}
/** 点击图表柱子或折线点后，仅展示对应统计周期内创建的商机。 */
const handleChartClick = (params: Record<string, unknown>) => {
  const time = typeof params.name === 'string' ? params.name : undefined
  const timeRange = time ? timeRangeMap.get(time) : undefined
  if (!timeRange) return
  queryParams0.pageNo = 1
  queryParams0.times = [...timeRange]
  getList()
}

/** 打开客户详情 */
const { push } = useRouter()
const openDetail = (id: number) => {
  push({ name: 'CrmBusinessDetail', params: { id } })
}

/** 打开客户详情 */
const openCustomerDetail = (id: number) => {
  push({ name: 'CrmCustomerDetail', params: { id } })
}

/** 获取统计数据 */
const loadData = async () => {
  loading.value = true
  try {
    await fetchAndFill()
  } finally {
    loading.value = false
  }
}

defineExpose({ loadData })

/** 初始�?*/
onMounted(() => {
  loadData()
})
</script>