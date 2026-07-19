<template>
  <ContentWrap>
    <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
      <div class="bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm">{{ t('crm.order.totalOrders') }}</p>
            <p class="text-2xl font-bold text-gray-800 mt-1">{{ statistics.totalCount || 0 }}</p>
          </div>
          <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
            <Icon icon="ep:order" class="w-6 h-6 text-blue-500" />
          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm">{{ t('crm.order.totalAmount') }}</p>
            <p class="text-2xl font-bold text-gray-800 mt-1">{{ formatMoney(statistics.totalAmount) }}</p>
          </div>
          <div class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
            <Icon icon="ep:money" class="w-6 h-6 text-green-500" />
          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm">{{ t('crm.order.pendingAudit') }}</p>
            <p class="text-2xl font-bold text-yellow-500 mt-1">{{ statistics.pendingAuditCount || 0 }}</p>
          </div>
          <div class="w-12 h-12 bg-yellow-100 rounded-full flex items-center justify-center">
            <Icon icon="ep:clock" class="w-6 h-6 text-yellow-500" />
          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg shadow p-4">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm">{{ t('crm.order.approved') }}</p>
            <p class="text-2xl font-bold text-green-500 mt-1">{{ statistics.approvedCount || 0 }}</p>
          </div>
          <div class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
            <Icon icon="ep:success" class="w-6 h-6 text-green-500" />
          </div>
        </div>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div class="bg-white rounded-lg shadow p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">{{ t('crm.order.statusDistribution') }}</h3>
        <div class="h-64">
          <Echart :options="pieChartOptions" height="256px" />
        </div>
      </div>

      <div class="bg-white rounded-lg shadow p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">{{ t('crm.order.recentOrders') }}</h3>
        <el-table :data="recentOrders" stripe>
          <el-table-column prop="no" :label="t('crm.order.no')" />
          <el-table-column prop="customerName" :label="t('crm.order.customer')" />
          <el-table-column prop="totalPrice" :label="t('crm.order.totalPrice')">
            <template #default="{ row }">
              {{ formatMoney(row.totalPrice) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" :label="t('crm.order.status')">
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { request } from '@/config/axios'
import { Echart } from '@/components/Echart'
import { Icon } from '@/components/Icon'
import { getOrderStatistics, getOrderPage } from '@/api/crm/order'

const { t } = useI18n()

const statistics = ref({
  totalCount: 0,
  totalAmount: 0,
  draftCount: 0,
  pendingAuditCount: 0,
  auditingCount: 0,
  approvedCount: 0,
  rejectedCount: 0,
  completedCount: 0,
  cancelledCount: 0
})

const recentOrders = ref<any[]>([])

const pieChartOptions = computed(() => {
  return {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'horizontal',
      bottom: 0,
      left: 'center',
      itemWidth: 12,
      itemHeight: 12,
      textStyle: {
        fontSize: 12
      }
    },
    series: [
      {
        name: t('crm.order.status'),
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['50%', '40%'],
        label: {
          show: false
        },
        labelLine: {
          show: false
        },
        data: [
          { value: statistics.value.draftCount, name: t('crm.order.statusDraft'), itemStyle: { color: '#909399' } },
          { value: statistics.value.pendingAuditCount, name: t('crm.order.statusPendingAudit'), itemStyle: { color: '#E6A23C' } },
          { value: statistics.value.auditingCount, name: t('crm.order.statusAuditing'), itemStyle: { color: '#409EFF' } },
          { value: statistics.value.approvedCount, name: t('crm.order.statusApproved'), itemStyle: { color: '#67C23A' } },
          { value: statistics.value.rejectedCount, name: t('crm.order.statusRejected'), itemStyle: { color: '#F56C6C' } },
          { value: statistics.value.completedCount, name: t('crm.order.statusCompleted'), itemStyle: { color: '#10B981' } },
          { value: statistics.value.cancelledCount, name: t('crm.order.statusCancelled'), itemStyle: { color: '#C0C4CC' } }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
})

const formatMoney = (value: number) => {
  if (!value) return '¥0.00'
  return '¥' + Number(value).toFixed(2)
}

const getStatusTagType = (status: number) => {
  const types: Record<number, string> = {
    0: 'info',
    10: 'warning',
    20: 'primary',
    30: 'success',
    40: 'danger',
    50: 'success',
    60: 'info'
  }
  return types[status] || 'info'
}

const getStatusLabel = (status: number) => {
  const labels: Record<number, string> = {
    0: t('crm.order.statusDraft'),
    10: t('crm.order.statusPendingAudit'),
    20: t('crm.order.statusAuditing'),
    30: t('crm.order.statusApproved'),
    40: t('crm.order.statusRejected'),
    50: t('crm.order.statusCompleted'),
    60: t('crm.order.statusCancelled')
  }
  return labels[status] || ''
}

const loadStatistics = async () => {
  try {
    const data = await getOrderStatistics()
    statistics.value = data
  } catch (error) {
    console.error('加载订单统计失败:', error)
  }
}

const loadRecentOrders = async () => {
  try {
    const data = await getOrderPage({ pageSize: 5 })
    recentOrders.value = data.list || []
  } catch (error) {
    console.error('加载最近订单失败:', error)
  }
}

onMounted(() => {
  loadStatistics()
  loadRecentOrders()
})
</script>