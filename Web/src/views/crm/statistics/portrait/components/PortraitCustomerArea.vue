<!-- 客户城市分布 -->
<template>
  <el-card shadow="never">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-skeleton :loading="loading" animated>
          <Echart :height="500" :options="echartsOption" />
        </el-skeleton>
      </el-col>
      <el-col :span="12">
        <el-skeleton :loading="loading" animated>
          <Echart :height="500" :options="echartsOption2" />
        </el-skeleton>
      </el-col>
    </el-row>
  </el-card>
</template>
<script lang="ts" setup>
import { EChartsOption } from 'echarts'
import china from '@/assets/map/json/china.json'
import echarts from '@/plugins/echarts'
import {
  CrmStatisticCustomerAreaRespVO,
  StatisticsPortraitApi
} from '@/api/crm/statistics/portrait'
import { areaReplace } from '@/utils'

defineOptions({ name: 'PortraitCustomerArea' })

const { t } = useI18n('crm.statistics')
const props = defineProps<{ queryParams: any }>()

echarts?.registerMap('china', china as any)

const loading = ref(false)
const areaStatisticsList = ref<CrmStatisticCustomerAreaRespVO[]>([])

// 绿色阶：浅 -> 深
const GREEN_COLORS = ['#a5d6a7', '#66bb6a', '#388e3c', '#1b5e20']

function pickColor(value: number, min: number, max: number): string {
  if (max === min) return GREEN_COLORS[GREEN_COLORS.length - 1]
  const ratio = (value - min) / (max - min)
  const idx = Math.min(Math.floor(ratio * GREEN_COLORS.length), GREEN_COLORS.length - 1)
  return GREEN_COLORS[idx]
}

/** all-customer map */
const echartsOption = reactive<EChartsOption>({
  title: { text: t('portrait.allCustomer'), left: 'center' },
  tooltip: {
    trigger: 'item',
    showDelay: 0,
    transitionDuration: 0.2,
    formatter: (params: any) => {
      const count = params.value || 0
      return count > 0
        ? `<b>${params.name}</b><br/>客户数量：${count}`
        : `<b>${params.name}</b><br/>暂无客户`
    }
  },
  series: [{
    name: t('portrait.areaDistribution'),
    type: 'map',
    map: 'china',
    roam: false,
    selectedMode: false,
    itemStyle: { areaColor: '#eeeeee', borderColor: '#bdbdbd' },
    emphasis: {
      label: { show: true, fontWeight: 'bold' },
      itemStyle: { areaColor: '#ff9800' }
    },
    data: []
  }]
}) as EChartsOption

/** deal-customer map */
const echartsOption2 = reactive<EChartsOption>({
  title: { text: t('portrait.dealCustomer'), left: 'center' },
  tooltip: {
    trigger: 'item',
    showDelay: 0,
    transitionDuration: 0.2,
    formatter: (params: any) => {
      const count = params.value || 0
      return count > 0
        ? `<b>${params.name}</b><br/>成交数量：${count}`
        : `<b>${params.name}</b><br/>暂无成交`
    }
  },
  series: [{
    name: t('portrait.areaDistribution'),
    type: 'map',
    map: 'china',
    roam: false,
    selectedMode: false,
    itemStyle: { areaColor: '#eeeeee', borderColor: '#bdbdbd' },
    emphasis: {
      label: { show: true, fontWeight: 'bold' },
      itemStyle: { areaColor: '#ff9800' }
    },
    data: []
  }]
}) as EChartsOption

const loadData = async () => {
  loading.value = true
  const areaList = await StatisticsPortraitApi.getCustomerArea(props.queryParams)
  areaStatisticsList.value = areaList.map((item: CrmStatisticCustomerAreaRespVO) => ({
    ...item,
    areaName: areaReplace(item.areaName)
  }))
  buildLeftMap()
  buildRightMap()
  loading.value = false
}
defineExpose({ loadData })

const buildLeftMap = () => {
  const vals = areaStatisticsList.value
    .filter((item) => (item.customerCount || 0) > 0)
  if (vals.length === 0) {
    echartsOption.series![0].data = []
    return
  }
  const nums = vals.map((v) => v.customerCount || 0)
  const min = Math.min(...nums)
  const max = Math.max(...nums)
  echartsOption.series![0].data = vals.map((item) => ({
    name: item.areaName,
    value: item.customerCount || 0,
    itemStyle: { areaColor: pickColor(item.customerCount || 0, min, max) }
  }))
}

const buildRightMap = () => {
  const vals = areaStatisticsList.value
    .filter((item) => (item.dealCount || 0) > 0)
  if (vals.length === 0) {
    echartsOption2.series![0].data = []
    return
  }
  const nums = vals.map((v) => v.dealCount || 0)
  const min = Math.min(...nums)
  const max = Math.max(...nums)
  echartsOption2.series![0].data = vals.map((item) => ({
    name: item.areaName,
    value: item.dealCount || 0,
    itemStyle: { areaColor: pickColor(item.dealCount || 0, min, max) }
  }))
}

onMounted(() => {
  loadData()
})
</script>
