<template>
  <el-page-header @back="back" :title="t('crm.order.detail')">
    <template #extra>
      <el-button
        v-if="orderData.status === 10"
        v-hasPermi="['crm:order:approval']"
        type="primary"
        @click="handleApproval"
      >
        {{ t('common.approve') }}
      </el-button>
    </template>
  </el-page-header>
  <ContentWrap>
    <div class="detail-info">
      <el-descriptions :column="3" border>
        <el-descriptions-item :label="t('crm.order.no')">{{ orderData.no }}</el-descriptions-item>
        <el-descriptions-item :label="t('crm.order.status')">
          <el-tag
            v-if="orderData.status === 0"
            type="info"
          >
            {{ t('crm.order.statusDraft') }}
          </el-tag>
          <el-tag
            v-else-if="orderData.status === 10"
            type="warning"
          >
            {{ t('crm.order.statusPendingAudit') }}
          </el-tag>
          <el-tag
            v-else-if="orderData.status === 20"
            type="primary"
          >
            {{ t('crm.order.statusAuditing') }}
          </el-tag>
          <el-tag
            v-else-if="orderData.status === 30"
            type="success"
          >
            {{ t('crm.order.statusApproved') }}
          </el-tag>
          <el-tag
            v-else-if="orderData.status === 40"
            type="danger"
          >
            {{ t('crm.order.statusRejected') }}
          </el-tag>
          <el-tag
            v-else-if="orderData.status === 50"
            type="success"
          >
            {{ t('crm.order.statusCompleted') }}
          </el-tag>
          <el-tag
            v-else-if="orderData.status === 60"
            type="danger"
          >
            {{ t('crm.order.statusCancelled') }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item :label="t('crm.order.customerName')">
          <el-link
            :underline="false"
            type="primary"
            @click="openCustomerDetail(orderData.customerId)"
          >
            {{ orderData.customerName }}
          </el-link>
        </el-descriptions-item>
        <el-descriptions-item :label="t('crm.order.contractName')">
          <el-link
            v-if="orderData.contractId"
            :underline="false"
            type="primary"
            @click="openContractDetail(orderData.contractId)"
          >
            {{ orderData.contractName }}
          </el-link>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item :label="t('crm.order.ownerUserName')">{{ orderData.ownerUserName }}</el-descriptions-item>
        <el-descriptions-item :label="t('crm.order.orderTime')">
          {{ formatDate(orderData.orderTime) }}
        </el-descriptions-item>
        <el-descriptions-item :label="t('crm.order.totalProductPrice')">
          {{ erpPriceInputFormatter(orderData.totalProductPrice) }}
        </el-descriptions-item>
        <el-descriptions-item :label="t('crm.order.discountPercent')">{{ orderData.discountPercent }}%</el-descriptions-item>
        <el-descriptions-item :label="t('crm.order.totalPrice')">
          <span class="price">{{ erpPriceInputFormatter(orderData.totalPrice) }}</span>
        </el-descriptions-item>
        <el-descriptions-item :label="t('crm.order.creatorName')">{{ orderData.creatorName }}</el-descriptions-item>
        <el-descriptions-item :label="t('crm.order.createTime')">
          {{ formatDate(orderData.createTime) }}
        </el-descriptions-item>
        <el-descriptions-item :label="t('crm.order.remark')" :span="2">
          {{ orderData.remark || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </div>
  </ContentWrap>
  <ContentWrap>
    <el-tabs v-model="activeName">
      <el-tab-pane :label="t('crm.order.productList')" name="product">
        <el-table v-loading="loading" :data="orderData.products" :show-overflow-tooltip="true" :stripe="true">
          <el-table-column type="index" label="#" width="50" />
          <el-table-column :label="t('crm.order.productName')" prop="productName" min-width="150" />
          <el-table-column :label="t('crm.order.productNo')" prop="productNo" min-width="120" />
          <el-table-column :label="t('crm.order.productUnit')" prop="productUnit" min-width="80" />
          <el-table-column :label="t('crm.order.productPrice')" prop="productPrice" min-width="120" :formatter="erpPriceTableColumnFormatter" />
          <el-table-column :label="t('crm.order.count')" prop="count" min-width="80" />
          <el-table-column :label="t('crm.order.totalPrice')" prop="totalPrice" min-width="120" :formatter="erpPriceTableColumnFormatter" />
          <el-table-column :label="t('crm.order.remark')" prop="remark" min-width="200" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </ContentWrap>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { onMounted } from 'vue'
import * as OrderApi from '@/api/crm/order'
import { erpPriceInputFormatter, erpPriceTableColumnFormatter } from '@/utils'

defineOptions({ name: 'CrmOrderDetail' })

const { t } = useI18n()
const route = useRoute()
const router = useRouter()
const loading = ref(true)
const orderData = ref<OrderApi.OrderVO>({} as OrderApi.OrderVO)
const activeName = ref('product')

const formatDate = (date: Date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString()
}

const openCustomerDetail = (id: number) => {
  router.push({ name: 'CrmCustomerDetail', params: { id } })
}

const openContractDetail = (id: number) => {
  router.push({ name: 'CrmContractDetail', params: { id } })
}

const back = () => {
  router.back()
}

const message = useMessage()
const handleApproval = async () => {
  try {
    await message.confirm(t('crm.order.approveConfirm'))
    await OrderApi.startApproval(route.params.id as number)
    message.success(t('crm.order.approveSuccess'))
    back()
  } catch {}
}

onMounted(async () => {
  loading.value = true
  try {
    orderData.value = await OrderApi.getOrder(route.params.id as number)
  } finally {
    loading.value = false
  }
})
</script>
<style scoped>
.detail-info {
  margin-bottom: 16px;
}
.price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}
</style>
