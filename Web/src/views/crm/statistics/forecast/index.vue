<!-- 数据统计 - 销售预测 -->
<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form class="-mb-15px" :model="queryParams" ref="queryFormRef" label-width="auto">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="负责人" prop="userId">
            <el-select
              v-model="queryParams.userId"
              class="!w-240px"
              placeholder="请选择负责人"
              clearable
            >
              <el-option
                v-for="item in userOptions"
                :key="item.id"
                :label="item.nickname"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="10">
          <el-form-item label="预计成交日期" prop="dealTime">
            <el-date-picker
              v-model="queryParams.dealTime"
              class="!w-360px"
              type="daterange"
              value-format="YYYY-MM-DD HH:mm:ss"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
            />
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item>
            <el-button @click="handleQuery">
              <Icon icon="ep:search" class="mr-5px" /> 搜索
            </el-button>
            <el-button @click="resetQuery">
              <Icon icon="ep:refresh" class="mr-5px" /> 重置
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </ContentWrap>

  <!-- KPI 卡片 -->
  <el-row :gutter="16" class="mb-16px">
    <el-col :span="6">
      <el-card shadow="hover" v-loading="summaryLoading">
        <div class="kpi-card">
          <div class="kpi-label">商机数</div>
          <div class="kpi-value">{{ summary.businessCount }}</div>
        </div>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="hover" v-loading="summaryLoading">
        <div class="kpi-card">
          <div class="kpi-label">商机总金额</div>
          <div class="kpi-value">{{ erpPriceInputFormatter(summary.totalAmount) }} 元</div>
        </div>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="hover" v-loading="summaryLoading">
        <div class="kpi-card">
          <div class="kpi-label">加权预测金额</div>
          <div class="kpi-value highlight">{{ erpPriceInputFormatter(summary.forecastAmount) }} 元</div>
        </div>
      </el-card>
    </el-col>
    <el-col :span="6">
      <el-card shadow="hover" v-loading="summaryLoading">
        <div class="kpi-card">
          <div class="kpi-label">成交金额</div>
          <div class="kpi-value">{{ erpPriceInputFormatter(summary.wonAmount) }} 元</div>
        </div>
      </el-card>
    </el-col>
  </el-row>

  <!-- 各阶段预测金额分布柱状图 -->
  <el-card shadow="never" class="mb-16px">
    <template #header>
      <span>各阶段预测金额分布</span>
    </template>
    <el-skeleton :loading="chartLoading" animated>
      <Echart :height="400" :options="echartsOption" />
    </el-skeleton>
    <el-empty
      v-if="!chartLoading && chartEmpty"
      description="暂无图表数据"
    />
  </el-card>

  <!-- 明细表格 -->
  <ContentWrap>
    <el-table
      v-loading="listLoading"
      :data="list"
      :stripe="true"
      :show-overflow-tooltip="true"
      :table-layout="'auto'"
    >
      <el-table-column label="商机名称" prop="businessName" min-width="160" fixed="left" />
      <el-table-column label="客户" prop="customerName" min-width="140" />
      <el-table-column label="负责人" prop="ownerUserName" min-width="100" align="center" />
      <el-table-column label="阶段" prop="statusName" min-width="120" align="center" />
      <el-table-column label="阶段赢单率" prop="stagePercent" min-width="110" align="center">
        <template #default="scope">
          {{ scope.row.stagePercent != null ? scope.row.stagePercent + '%' : '-' }}
        </template>
      </el-table-column>
      <el-table-column
        label="商机金额（元）"
        prop="totalPrice"
        min-width="140"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column
        label="加权预测金额（元）"
        prop="forecastAmount"
        min-width="160"
        :formatter="erpPriceTableColumnFormatter"
      />
      <el-table-column
        label="预计成交日期"
        prop="dealTime"
        min-width="160"
        :formatter="dateFormatter"
      />
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNo"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </ContentWrap>
</template>

<script lang="ts" setup>
import { EChartsOption } from 'echarts'
import { dateFormatter } from '@/utils/formatTime'
import { erpPriceInputFormatter, erpPriceTableColumnFormatter } from '@/utils'
import * as UserApi from '@/api/system/user'
import { ForecastApi } from '@/api/crm/statistics/forecast'
import type { ForecastSummaryVO, ForecastDetailVO } from '@/api/crm/statistics/forecast'

defineOptions({ name: 'CrmStatisticsForecast' })

const message = useMessage() // 消息弹窗

/** 搜索参数 */
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10,
  userId: undefined as number | undefined,
  dealTime: [] as string[],
  beginDealTime: undefined as string | undefined,
  endDealTime: undefined as string | undefined
})
const queryFormRef = ref()

/** 用户列表 */
const userOptions = ref<UserApi.UserVO[]>([])

/** KPI 汇总 */
const summaryLoading = ref(false)
const summary = ref<ForecastSummaryVO>({
  businessCount: 0,
  totalAmount: 0,
  forecastAmount: 0,
  wonAmount: 0,
  wonCount: 0,
  avgWinRate: 0
})

/** 柱状图 */
const chartLoading = ref(false)
const chartEmpty = ref(false)
const echartsOption = reactive<EChartsOption>({
  grid: {
    left: 20,
    right: 30,
    bottom: 20,
    containLabel: true
  },
  legend: {},
  series: [
    {
      name: '预测金额',
      type: 'bar',
      data: [],
      itemStyle: { color: '#409EFF' }
    }
  ],
  toolbox: {
    feature: {
      saveAsImage: { show: true, name: '销售预测分布' }
    }
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  yAxis: {
    type: 'value',
    name: '金额（元）',
    min: 0
  },
  xAxis: {
    type: 'category',
    name: '阶段',
    data: []
  }
}) as EChartsOption

/** 明细表格 */
const listLoading = ref(false)
const list = ref<ForecastDetailVO[]>([])
const total = ref(0)

/** 获取 KPI 汇总 */
const getSummary = async () => {
  summaryLoading.value = true
  try {
    const data = await ForecastApi.getForecastSummary({
      userId: queryParams.userId,
      beginDealTime: queryParams.beginDealTime,
      endDealTime: queryParams.endDealTime
    })
    summary.value = data
  } catch (e) {
    // 忽略错误，KPI 不阻塞主流程
  } finally {
    summaryLoading.value = false
  }
}

/** 获取图表数据：按阶段汇总预测金额 */
const getChart = async () => {
  chartLoading.value = true
  chartEmpty.value = false
  try {
    // 复用明细数据，按阶段分组汇总
    const detailList = await ForecastApi.getForecastPage({
      ...queryParams,
      pageNo: 1,
      pageSize: 1000
    })
    const stageMap = new Map<string, number>()
    detailList.list.forEach((item: ForecastDetailVO) => {
      const stage = item.statusName || '未知'
      stageMap.set(stage, (stageMap.get(stage) || 0) + (item.forecastAmount || 0))
    })
    const stages = Array.from(stageMap.keys())
    const values = Array.from(stageMap.values())
    if (echartsOption.xAxis && !Array.isArray(echartsOption.xAxis)) {
      ;(echartsOption.xAxis as any).data = stages
    }
    if (echartsOption.series && echartsOption.series[0]) {
      ;(echartsOption.series[0] as any).data = values
    }
    chartEmpty.value = stages.length === 0
  } catch (e) {
    chartEmpty.value = true
  } finally {
    chartLoading.value = false
  }
}

/** 获取明细列表 */
const getList = async () => {
  listLoading.value = true
  try {
    const data = await ForecastApi.getForecastPage(queryParams)
    list.value = data.list
    total.value = data.total
  } catch (e) {
    message.error('获取预测明细失败')
  } finally {
    listLoading.value = false
  }
}

/** 加载全部数据 */
const loadAll = async () => {
  await Promise.all([getSummary(), getChart(), getList()])
}

/** 搜索 */
const handleQuery = () => {
  // 处理日期范围
  if (queryParams.dealTime && queryParams.dealTime.length === 2) {
    queryParams.beginDealTime = queryParams.dealTime[0]
    queryParams.endDealTime = queryParams.dealTime[1]
  } else {
    queryParams.beginDealTime = undefined
    queryParams.endDealTime = undefined
  }
  queryParams.pageNo = 1
  loadAll()
}

/** 重置 */
const resetQuery = () => {
  queryFormRef.value?.resetFields()
  queryParams.dealTime = []
  queryParams.beginDealTime = undefined
  queryParams.endDealTime = undefined
  handleQuery()
}

/** 初始化 */
onMounted(async () => {
  // 获得用户列表
  userOptions.value = await UserApi.getSimpleUserList()
  await loadAll()
})
</script>

<style lang="scss" scoped>
.kpi-card {
  text-align: center;
  padding: 10px 0;

  .kpi-label {
    font-size: 14px;
    color: var(--el-text-color-secondary);
    margin-bottom: 8px;
  }

  .kpi-value {
    font-size: 24px;
    font-weight: 600;
    color: var(--el-text-color-primary);

    &.highlight {
      color: var(--el-color-primary);
    }
  }
}
</style>
