<!-- 销售漏斗分析 -->
<template>
  <!-- Echarts图 -->
  <el-card shadow="never">
    <el-row>
      <el-col :span="24">
        <el-button-group class="mb-10px">
          <el-button type="primary" @click="handleActive(true)">{{ t('funnel.customerView') }}</el-button>
          <el-button type="primary" @click="handleActive(false)">{{ t('funnel.dynamicView') }}</el-button>
        </el-button-group>
        <el-skeleton :loading="loading" animated>
          <Echart :height="500" :options="echartsOption" @click="handleChartClick" />
        </el-skeleton>
      </el-col>
    </el-row>
  </el-card>
</template>
<script lang="ts" setup>
import { CrmStatisticFunnelRespVO, StatisticFunnelApi } from '@/api/crm/statistics/funnel'
import { EChartsOption } from 'echarts'
import { DICT_TYPE } from '@/utils/dict'

defineOptions({ name: 'FunnelBusiness' })

const { t } = useI18n('crm.statistics')

const props = defineProps<{ queryParams: any }>()

const active = ref(true)
const loading = ref(false)
const list = ref<CrmStatisticFunnelRespVO[]>([])

/** 销售漏斗 */
const echartsOption = reactive<EChartsOption>({
  title: {
    text: t('funnel.funnel')
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}'
  },
  toolbox: {
    feature: {
      dataView: { readOnly: false },
      restore: {},
      saveAsImage: {}
    }
  },
  legend: {
    data: [t('funnel.customer'), t('funnel.business'), t('funnel.win')]
  },
  series: [
    {
      name: t('funnel.funnel'),
      type: 'funnel',
      left: '10%',
      top: 60,
      bottom: 60,
      width: '80%',
      min: 0,
      max: 100,
      minSize: '0%',
      maxSize: '100%',
      sort: 'descending',
      gap: 2,
      label: {
        show: true,
        position: 'inside'
      },
      labelLine: {
        length: 10,
        lineStyle: {
          width: 1,
          type: 'solid'
        }
      },
      itemStyle: {
        borderColor: '#fff',
        borderWidth: 1
      },
      emphasis: {
        label: {
          fontSize: 20
        }
      },
      data: [
        { value: 60, name: `${t('funnel.customer')}-0${t('funnel.unit')}` },
        { value: 40, name: `${t('funnel.business')}-0${t('funnel.unit')}` },
        { value: 20, name: `${t('funnel.win')}-0${t('funnel.unit')}` }
      ]
    }
  ]
}) as EChartsOption

const handleActive = async (val: boolean) => {
  active.value = val
  await loadData()
}

/** 获取统计数据 */
const loadData = async () => {
  loading.value = true
  // 1. 加载漏斗数据
  const data = (await StatisticFunnelApi.getFunnelSummary(
    props.queryParams
  )) as CrmStatisticFunnelRespVO
  // 2.1 更新 Echarts 数据
  if (
    !!data &&
    echartsOption.series &&
    echartsOption.series[0] &&
    echartsOption.series[0]['data']
  ) {
    const list: { value: number; name: string }[] = []
    if (active.value) {
      list.push({ value: 60, name: `${t('funnel.customer')}-${data.customerCount || 0}${t('funnel.unit')}` })
      list.push({ value: 40, name: `${t('funnel.business')}-${data.businessCount || 0}${t('funnel.unit')}` })
      list.push({ value: 20, name: `${t('funnel.win')}-${data.businessWinCount || 0}${t('funnel.unit')}` })
    } else {
      list.push({ value: data.customerCount || 0, name: `${t('funnel.customer')}-${data.customerCount || 0}${t('funnel.unit')}` })
      list.push({ value: data.businessCount || 0, name: `${t('funnel.business')}-${data.businessCount || 0}${t('funnel.unit')}` })
      list.push({ value: data.businessWinCount || 0, name: `${t('funnel.win')}-${data.businessWinCount || 0}${t('funnel.unit')}` })
    }

    echartsOption.series[0]['data'] = list
  }
  // 2.2 获取商机结束状态统计
  list.value = await StatisticFunnelApi.getBusinessSummaryByEndStatus(props.queryParams)
  loading.value = false
}
defineExpose({ loadData })

/** 点击漏斗图形，刷新下方统计列表 */
const handleChartClick = (params: Record<string, unknown>) => {
  // 点击漏斗某一部分后，重新加载统计数据
  loadData()
}

/** 初始化 */
onMounted(() => {
  loadData()
})
</script>
