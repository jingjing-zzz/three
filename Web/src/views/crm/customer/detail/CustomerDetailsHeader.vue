<template>
  <div v-loading="loading">
    <div class="flex items-start justify-between">
      <div>
        <!-- 左上：客户基本信息 -->
        <el-col>
          <el-row>
            <span class="text-xl font-bold">{{ customer.name }}</span>
          </el-row>
        </el-col>
      </div>
      <div>
        <!-- 右上：操作按钮 -->
        <slot></slot>
      </div>
    </div>
  </div>
  <ContentWrap class="mt-10px">
    <el-descriptions :column="5" direction="vertical">
      <el-descriptions-item :label="t('customer.level')">
        <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_LEVEL" :value="customer.level" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('customer.source')">
        <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_SOURCE" :value="customer.source" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('customer.industryId')">
        <dict-tag :type="DICT_TYPE.CRM_CUSTOMER_INDUSTRY" :value="customer.industryId" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('customer.mobile')">
        {{ customer.mobile }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('customer.ownerUserId')">
        {{ customer.ownerUserName }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('customer.dealStatus')">
        <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="customer.dealStatus" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('customer.lockStatus')">
        <dict-tag :type="DICT_TYPE.INFRA_BOOLEAN_STRING" :value="customer.lockStatus" />
      </el-descriptions-item>
      <el-descriptions-item :label="t('customer.contactLastTime')">
        {{ formatDate(customer.contactLastTime) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('customer.contactNextTime')">
        {{ formatDate(customer.contactNextTime) }}
      </el-descriptions-item>
      <el-descriptions-item :label="t('customer.ownerUserDeptName')">
        {{ customer.ownerUserDeptName }}
      </el-descriptions-item>
    </el-descriptions>
  </ContentWrap>
</template>
<script lang="ts" setup>
import { DICT_TYPE } from '@/utils/dict'
import * as CustomerApi from '@/api/crm/customer'
import { formatDate } from '@/utils/formatTime'

defineOptions({ name: 'CrmCustomerDetailsHeader' })

const { t } = useI18n('crm') // 国际化

defineProps<{
  customer: CustomerApi.CustomerVO // 客户信息
  loading: boolean // 加载中
}>()
</script>
