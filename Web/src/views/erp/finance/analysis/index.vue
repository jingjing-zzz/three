<template>
  <ContentWrap>
    <el-form :model="queryParams" :inline="true" label-width="80px">
      <el-form-item label="业务时间">
        <el-date-picker
          v-model="queryParams.recordTime"
          value-format="YYYY-MM-DD HH:mm:ss"
          type="daterange"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
          class="!w-260px"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="getSummary"><Icon icon="ep:search" class="mr-5px" /> 查询</el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>
  <ContentWrap>
    <el-row :gutter="16">
      <el-col v-for="item in cards" :key="item.label" :xs="24" :sm="12" :md="8" :lg="6">
        <el-statistic :title="item.label" :value="item.value" :precision="2" />
      </el-col>
    </el-row>
  </ContentWrap>
</template>

<script setup lang="ts">
import { FinanceRecordApi, FinanceSummaryVO } from '@/api/erp/finance/record'

defineOptions({ name: 'ErpFinanceAnalysis' })

const queryParams = reactive({
  recordTime: undefined
})
const summary = ref<FinanceSummaryVO>({
  invoiceAmount: 0,
  reimbursementAmount: 0,
  refundAmount: 0,
  expenseAmount: 0,
  receiptAmount: 0,
  paymentAmount: 0,
  netAmount: 0,
  overdueCount: 0
})
const cards = computed(() => [
  { label: '已审核发票', value: Number(summary.value.invoiceAmount || 0) },
  { label: '已审核报销', value: Number(summary.value.reimbursementAmount || 0) },
  { label: '已审核退款', value: Number(summary.value.refundAmount || 0) },
  { label: '已审核费用', value: Number(summary.value.expenseAmount || 0) },
  { label: '已审核收款', value: Number(summary.value.receiptAmount || 0) },
  { label: '已审核付款', value: Number(summary.value.paymentAmount || 0) },
  { label: '收支净额', value: Number(summary.value.netAmount || 0) },
  { label: '逾期单据', value: Number(summary.value.overdueCount || 0) }
])

const getSummary = async () => {
  summary.value = await FinanceRecordApi.getSummary(queryParams)
}

onMounted(() => {
  getSummary()
})
</script>
