<template>
  <ContentWrap>
    <el-collapse v-model="activeNames">
      <el-collapse-item name="basicInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('crm.customer.basicInfoTab') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('crm.business.name')">{{ business.name }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.customerName')">{{ business.customerName }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.price') + '（元）'">
            {{ erpPriceInputFormatter(business.totalPrice) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.dealTime')">
            {{ formatDate(business.dealTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.contactNextTime')">
            {{ formatDate(business.contactNextTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.statusTypeId')">
            {{ business.statusTypeName }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.statusName')">{{ business.statusName }}</el-descriptions-item>
          <el-descriptions-item label="商机来源">{{ getSourceLabel(business.source) }}</el-descriptions-item>
          <el-descriptions-item label="竞争对手">{{ business.competitor }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.remark')">{{ business.remark }}</el-descriptions-item>
          <el-descriptions-item :label="''">&nbsp;</el-descriptions-item>
          <el-descriptions-item :label="''">&nbsp;</el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>

      <el-collapse-item name="stageFlow">
        <template #title>
          <span class="text-base font-bold">阶段流转</span>
        </template>
        <div class="stage-flow">
          <el-steps v-if="stageList.length > 0" :active="currentStageIndex" align-center>
            <el-step v-for="stage in stageList" :key="stage.id" :title="stage.name">
              <template #description>
                {{ stage.percent }}% 成功率
              </template>
            </el-step>
          </el-steps>
          <el-empty v-else description="当前状态组未配置阶段" :image-size="80" />
        </div>
      </el-collapse-item>

      <el-collapse-item name="systemInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('common.systemInfo') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('crm.business.ownerUserName')">{{ business.ownerUserName }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.contactLastTime')">
            {{ formatDate(business.contactLastTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="''">&nbsp;</el-descriptions-item>
          <el-descriptions-item :label="''">&nbsp;</el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.creatorName')">{{ business.creatorName }}</el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.createTime')">
            {{ formatDate(business.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('crm.business.updateTime')">
            {{ formatDate(business.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
    </el-collapse>
  </ContentWrap>
</template>
<script setup lang="ts">
import * as BusinessApi from '@/api/crm/business'
import * as DictDataApi from '@/api/system/dict/dict.data'
import * as BusinessStatusApi from '@/api/crm/business/status'
import { formatDate } from '@/utils/formatTime'
import { erpPriceInputFormatter } from '@/utils'

const { t } = useI18n()

const { business } = defineProps<{
  business: BusinessApi.BusinessVO
}>()

const activeNames = ref(['basicInfo', 'stageFlow', 'systemInfo'])

type BusinessStage = {
  id: number
  name: string
  percent: number
}

const stageList = ref<BusinessStage[]>([])
const currentStageIndex = computed(() => {
  if (business.endStatus) {
    return stageList.value.length
  }
  const index = stageList.value.findIndex((stage) => stage.id === business.statusId)
  return index >= 0 ? index : 0
})

const loadStageList = async () => {
  if (!business.statusTypeId) {
    stageList.value = []
    return
  }
  stageList.value = await BusinessStatusApi.getBusinessStatusSimpleList(business.statusTypeId)
}

const sourceDictMap = ref<Record<string, string>>({})

const loadSourceDict = async () => {
  const list = await DictDataApi.getDictDataByType('crm_business_source')
  list.forEach(item => {
    sourceDictMap.value[item.value] = item.label
  })
}

const getSourceLabel = (source: string) => {
  return sourceDictMap.value[source] || source
}

watch(() => business.statusTypeId, loadStageList, { immediate: true })
loadSourceDict()
</script>