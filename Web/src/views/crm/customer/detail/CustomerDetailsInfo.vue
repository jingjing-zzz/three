<template>
  <ContentWrap>
    <el-collapse v-model="activeNames">
      <el-collapse-item name="basicInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('customer.basicInfoTab') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('customer.name')">
            {{ customer.name }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.source')">
            <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_SOURCE" :value="customer.source" />
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.mobile')">
            {{ customer.mobile }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.telephone')">
            {{ customer.telephone }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.email')">
            {{ customer.email }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.qq')">
            {{ customer.qq }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.wechat')">
            {{ customer.wechat }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.industryId')">
            <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_INDUSTRY" :value="customer.industryId" />
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.level')">
            <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_LEVEL" :value="customer.level" />
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.areaId')">
            {{ customer.areaName }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.detailAddress')">
            {{ customer.detailAddress }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.contactLastTime')">
            {{ formatDate(customer.contactLastTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.contactNextTime')">
            {{ formatDate(customer.contactNextTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.contactLastContent')">
            {{ customer.contactLastContent }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.remark')">
            {{ customer.remark }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.dealStatus')">
            <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="customer.dealStatus" />
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.lockStatus')">
            <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="customer.lockStatus" />
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
      <el-collapse-item name="systemInfo">
        <template #title>
          <span class="text-base font-bold">{{ t('receivable.systemInfo') }}</span>
        </template>
        <el-descriptions :column="4">
          <el-descriptions-item :label="t('customer.ownerUserId')">
            {{ customer.ownerUserName }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.ownerUserDeptName')">
            {{ customer.ownerUserDeptName }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('customer.followUpStatus')">
            <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="customer.followUpStatus" />
          </el-descriptions-item>
          <el-descriptions-item :label="t('common.creator')">
            {{ customer.creatorName }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('common.createTime')">
            {{ formatDate(customer.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item :label="t('common.updateTime')">
            {{ formatDate(customer.updateTime) }}
          </el-descriptions-item>
        </el-descriptions>
      </el-collapse-item>
    </el-collapse>
  </ContentWrap>
</template>
<script lang="ts" setup>
import * as CustomerApi from '@/api/crm/customer'
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'

defineOptions({ name: 'CrmCustomerDetailsInfo' })

const { t } = useI18n('crm') // 国际化

const { customer } = defineProps<{
  customer: CustomerApi.CustomerVO // 客户详情
}>()

const activeNames = ref(['basicInfo', 'systemInfo']) // 默认展开的折叠面板
</script>
<style lang="scss" scoped></style>
