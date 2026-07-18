<template>
  <ContentWrap>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="statistics-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm">工单总数</p>
              <p class="text-3xl font-bold mt-2">{{ statistics.totalCount || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-blue-50 rounded-full flex items-center justify-center">
              <Icon icon="ep:document" class="w-6 h-6 text-blue-500" />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="statistics-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm">待处理</p>
              <p class="text-3xl font-bold mt-2 text-yellow-500">{{ statistics.pendingCount || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-yellow-50 rounded-full flex items-center justify-center">
              <Icon icon="ep:clock" class="w-6 h-6 text-yellow-500" />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="statistics-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm">处理中</p>
              <p class="text-3xl font-bold mt-2 text-blue-500">{{ statistics.processingCount || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-blue-50 rounded-full flex items-center justify-center">
              <Icon icon="ep:setting" class="w-6 h-6 text-blue-500" />
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="statistics-card">
          <div class="flex items-center justify-between">
            <div>
              <p class="text-gray-500 text-sm">已完结</p>
              <p class="text-3xl font-bold mt-2 text-green-500">{{ statistics.completedCount || 0 }}</p>
            </div>
            <div class="w-12 h-12 bg-green-50 rounded-full flex items-center justify-center">
              <Icon icon="ep:success" class="w-6 h-6 text-green-500" />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </ContentWrap>

  <ContentWrap>
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card header="按类型统计">
          <div class="space-y-4">
            <div
              v-for="item in statistics.typeStatistics"
              :key="item.type"
              class="flex items-center"
            >
              <span class="w-20">{{ item.typeName }}</span>
              <div class="flex-1 h-8 bg-gray-100 rounded-full overflow-hidden mr-4">
                <div
                  class="h-full bg-gradient-to-r from-blue-400 to-blue-600 transition-all duration-500"
                  :style="{ width: getPercentage(item.count, statistics.totalCount) + '%' }"
                ></div>
              </div>
              <span class="w-12 text-right font-bold">{{ item.count }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="按优先级统计">
          <div class="space-y-4">
            <div
              v-for="item in statistics.priorityStatistics"
              :key="item.priority"
              class="flex items-center"
            >
              <span class="w-20">{{ item.priorityName }}</span>
              <div class="flex-1 h-8 bg-gray-100 rounded-full overflow-hidden mr-4">
                <div
                  class="h-full transition-all duration-500"
                  :class="getPriorityBgClass(item.priority)"
                  :style="{ width: getPercentage(item.count, statistics.totalCount) + '%' }"
                ></div>
              </div>
              <span class="w-12 text-right font-bold">{{ item.count }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </ContentWrap>

  <ContentWrap>
    <el-card header="状态分布">
      <div class="flex items-center justify-center space-x-12">
        <div
          v-for="item in statusList"
          :key="item.value"
          class="flex flex-col items-center"
        >
          <div
            class="w-24 h-24 rounded-full flex items-center justify-center mb-4"
            :class="getStatusBgClass(item.value)"
          >
            <span class="text-white text-xl font-bold">{{ getStatusCount(item.value) }}</span>
          </div>
          <span>{{ item.label }}</span>
        </div>
      </div>
    </el-card>
  </ContentWrap>
</template>

<script lang="ts" setup>
import { onMounted, reactive } from 'vue'
import * as WorkOrderApi from '@/api/crm/workOrder'
import { WORK_ORDER_STATUS_OPTIONS } from '@/api/crm/workOrder'

defineOptions({ name: 'CrmWorkOrderStatistics' })

const statistics = reactive({
  totalCount: 0,
  pendingCount: 0,
  processingCount: 0,
  completedCount: 0,
  rejectedCount: 0,
  typeStatistics: [],
  priorityStatistics: []
})

const statusList = WORK_ORDER_STATUS_OPTIONS

const getPercentage = (count: number, total: number) => {
  if (total === 0) return 0
  return Math.round((count / total) * 100)
}

const getPriorityBgClass = (priority: number) => {
  switch (priority) {
    case 1: return 'bg-gradient-to-r from-red-400 to-red-600'
    case 2: return 'bg-gradient-to-r from-orange-400 to-orange-600'
    case 3: return 'bg-gradient-to-r from-blue-400 to-blue-600'
    case 4: return 'bg-gradient-to-r from-green-400 to-green-600'
    default: return 'bg-gray-400'
  }
}

const getStatusBgClass = (status: number) => {
  switch (status) {
    case 1: return 'bg-yellow-500'
    case 2: return 'bg-blue-500'
    case 3: return 'bg-green-500'
    case 4: return 'bg-red-500'
    default: return 'bg-gray-400'
  }
}

const getStatusCount = (status: number) => {
  switch (status) {
    case 1: return statistics.pendingCount
    case 2: return statistics.processingCount
    case 3: return statistics.completedCount
    case 4: return statistics.rejectedCount
    default: return 0
  }
}

const getStatistics = async () => {
  const data = await WorkOrderApi.getWorkOrderStatistics()
  Object.assign(statistics, data)
}

onMounted(() => {
  getStatistics()
})
</script>

<style scoped>
.statistics-card {
  transition: transform 0.2s, box-shadow 0.2s;
}
.statistics-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
</style>