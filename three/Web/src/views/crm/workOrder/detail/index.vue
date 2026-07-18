<template>
  <ContentWrap>
    <div class="work-order-detail">
      <div class="detail-header">
        <div class="header-info">
          <h2 class="title">{{ workOrder.title }}</h2>
          <div class="header-tags">
            <el-tag :type="getTypeTagType(workOrder.type)" size="small">{{ workOrder.typeName }}</el-tag>
            <el-tag :type="getPriorityTagType(workOrder.priority)" size="small">{{ workOrder.priorityName }}</el-tag>
            <el-tag :type="getStatusTagType(workOrder.status)" size="small">{{ workOrder.statusName }}</el-tag>
          </div>
        </div>
        <div class="header-actions">
          <el-button v-hasPermi="['crm:work-order:update']" @click="openForm('update', workOrder.id)">
            {{ t('common.edit') }}
          </el-button>
          <el-button v-if="workOrder.status === 1" v-hasPermi="['crm:work-order:update']" type="success" @click="handleStartProcess">
            开始处理
          </el-button>
          <el-button v-if="workOrder.status === 2" v-hasPermi="['crm:work-order:update']" type="success" @click="openCompleteDialog">
            完结
          </el-button>
          <el-button v-if="workOrder.status === 2" v-hasPermi="['crm:work-order:update']" type="warning" @click="openRejectDialog">
            退回
          </el-button>
          <el-button v-if="workOrder.status === 4" v-hasPermi="['crm:work-order:update']" type="success" @click="handleRestart">
            重新发起
          </el-button>
        </div>
      </div>

      <el-row :gutter="20">
        <el-col :span="16">
          <el-card class="info-card">
            <template #header>
              <span>工单内容</span>
            </template>
            <p class="content-text">{{ workOrder.content }}</p>
          </el-card>

          <el-card class="info-card">
            <template #header>
              <span>处理结果</span>
            </template>
            <p v-if="workOrder.result" class="content-text">{{ workOrder.result }}</p>
            <p v-else class="text-gray-400">暂无处理结果</p>
          </el-card>

          <el-card class="info-card">
            <template #header>
              <span>退回原因</span>
            </template>
            <p v-if="workOrder.rejectReason" class="content-text">{{ workOrder.rejectReason }}</p>
            <p v-else class="text-gray-400">暂无退回原因</p>
          </el-card>

          <el-card class="info-card">
            <template #header>
              <span>状态变更记录</span>
            </template>
            <el-timeline v-if="workOrder.logs && workOrder.logs.length > 0">
              <el-timeline-item
                v-for="(log, index) in workOrder.logs"
                :key="index"
                :timestamp="formatTime(log.createTime)"
                placement="top"
              >
                <el-card size="small">
                  <div class="log-content">
                    <span class="log-status" :class="getStatusTagType(log.statusBefore)">
                      {{ log.statusBeforeName }}
                    </span>
                    <span class="log-arrow">→</span>
                    <span class="log-status" :class="getStatusTagType(log.statusAfter)">
                      {{ log.statusAfterName }}
                    </span>
                    <span v-if="log.operatorName" class="log-operator">操作人：{{ log.operatorName }}</span>
                    <span v-if="log.remark" class="log-remark">备注：{{ log.remark }}</span>
                  </div>
                </el-card>
              </el-timeline-item>
            </el-timeline>
            <p v-else class="text-gray-400 text-center py-4">暂无状态变更记录</p>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card class="info-card">
            <template #header>
              <span>基本信息</span>
            </template>
            <dl class="info-list">
              <dt>创建人</dt>
              <dd>{{ workOrder.creatorName || '-' }}</dd>
              <dt>创建时间</dt>
              <dd>{{ formatTime(workOrder.createTime) }}</dd>
              <dt>更新时间</dt>
              <dd>{{ formatTime(workOrder.updateTime) }}</dd>
              <dt>客户</dt>
              <dd>{{ workOrder.customerName || '-' }}</dd>
              <dt>处理人</dt>
              <dd>{{ workOrder.ownerUserName || '-' }}</dd>
              <dt>处理时间</dt>
              <dd>{{ formatTime(workOrder.processTime) || '-' }}</dd>
              <dt>完结时间</dt>
              <dd>{{ formatTime(workOrder.completeTime) || '-' }}</dd>
            </dl>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </ContentWrap>

  <WorkOrderForm ref="formRef" @success="getWorkOrder" />

  <el-dialog v-model="completeDialogVisible" title="完结工单" width="500px">
    <el-form ref="completeFormRef" :model="completeForm" label-width="auto">
      <el-form-item label="处理结果" prop="result">
        <el-input type="textarea" v-model="completeForm.result" placeholder="请输入处理结果" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="completeDialogVisible = false">{{ t('common.cancel') }}</el-button>
      <el-button type="primary" @click="handleComplete">{{ t('common.confirm') }}</el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="rejectDialogVisible" title="退回工单" width="500px">
    <el-form ref="rejectFormRef" :model="rejectForm" label-width="auto">
      <el-form-item label="退回原因" prop="rejectReason">
        <el-input type="textarea" v-model="rejectForm.rejectReason" placeholder="请输入退回原因" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="rejectDialogVisible = false">{{ t('common.cancel') }}</el-button>
      <el-button type="primary" @click="handleReject">{{ t('common.confirm') }}</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTagsViewStore } from '@/store/modules/tagsView'
import * as WorkOrderApi from '@/api/crm/workOrder'
import WorkOrderForm from '../WorkOrderForm.vue'

defineOptions({ name: 'CrmWorkOrderDetail' })

const { t } = useI18n()
const message = useMessage()
const { params } = useRoute()
const { delView, currentRoute } = useTagsViewStore()
const { push } = useRouter()

const workOrderId = ref(0)
const loading = ref(true)
const workOrder = ref<WorkOrderApi.WorkOrderVO>({} as WorkOrderApi.WorkOrderVO)

const formRef = ref()

const completeDialogVisible = ref(false)
const completeFormRef = ref()
const completeForm = reactive({
  id: null,
  result: ''
})

const rejectDialogVisible = ref(false)
const rejectFormRef = ref()
const rejectForm = reactive({
  id: null,
  rejectReason: ''
})

const getTypeTagType = (type: number) => {
  switch (type) {
    case 1: return 'info'
    case 2: return 'danger'
    case 3: return 'success'
    case 4: return 'warning'
    default: return 'default'
  }
}

const getPriorityTagType = (priority: number) => {
  switch (priority) {
    case 1: return 'danger'
    case 2: return 'warning'
    case 3: return 'info'
    case 4: return 'success'
    default: return 'default'
  }
}

const getStatusTagType = (status: number) => {
  switch (status) {
    case 1: return 'warning'
    case 2: return 'primary'
    case 3: return 'success'
    case 4: return 'danger'
    default: return 'default'
  }
}

const formatTime = (time: Date) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const getWorkOrder = async () => {
  loading.value = true
  try {
    workOrder.value = await WorkOrderApi.getWorkOrder(workOrderId.value)
  } finally {
    loading.value = false
  }
}

const openForm = (type: string, id?: number) => {
  formRef.value.open(type, id)
}

const handleStartProcess = async () => {
  try {
    await WorkOrderApi.updateWorkOrderStatus({ id: workOrder.value.id, status: 2 })
    message.success('已开始处理')
    await getWorkOrder()
  } catch (e) {
    message.error('操作失败')
  }
}

const openCompleteDialog = () => {
  completeForm.id = workOrder.value.id
  completeForm.result = ''
  completeDialogVisible.value = true
}

const handleComplete = async () => {
  try {
    await WorkOrderApi.updateWorkOrderStatus({
      id: completeForm.id,
      status: 3,
      result: completeForm.result
    })
    message.success('工单已完结')
    completeDialogVisible.value = false
    await getWorkOrder()
  } catch (e) {
    message.error('操作失败')
  }
}

const openRejectDialog = () => {
  rejectForm.id = workOrder.value.id
  rejectForm.rejectReason = ''
  rejectDialogVisible.value = true
}

const handleReject = async () => {
  try {
    await WorkOrderApi.updateWorkOrderStatus({
      id: rejectForm.id,
      status: 4,
      rejectReason: rejectForm.rejectReason
    })
    message.success('工单已退回')
    rejectDialogVisible.value = false
    await getWorkOrder()
  } catch (e) {
    message.error('操作失败')
  }
}

const handleRestart = async () => {
  try {
    await WorkOrderApi.updateWorkOrderStatus({ id: workOrder.value.id, status: 1 })
    message.success('工单已重新发起')
    await getWorkOrder()
  } catch (e) {
    message.error('操作失败')
  }
}

const close = () => {
  delView(unref(currentRoute))
}

onMounted(async () => {
  if (!params.id) {
    message.warning('参数错误')
    close()
    return
  }
  workOrderId.value = params.id as unknown as number
  await getWorkOrder()
})
</script>

<style scoped>
.work-order-detail {
  .detail-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;
    padding-bottom: 20px;
    border-bottom: 1px solid #eee;
  }

  .header-info {
    .title {
      font-size: 20px;
      font-weight: bold;
      margin-bottom: 10px;
      color: #333;
    }

    .header-tags {
      display: flex;
      gap: 8px;
    }
  }

  .header-actions {
    display: flex;
    gap: 8px;
  }

  .info-card {
    margin-bottom: 20px;
  }

  .content-text {
    font-size: 14px;
    line-height: 1.8;
    color: #666;
    white-space: pre-wrap;
  }

  .info-list {
    margin: 0;
    padding: 0;

    dt {
      float: left;
      width: 80px;
      clear: left;
      font-weight: bold;
      color: #999;
      font-size: 14px;
      padding: 8px 0;
    }

    dd {
      margin-left: 90px;
      padding: 8px 0;
      font-size: 14px;
      color: #333;
      border-bottom: 1px dashed #f0f0f0;
    }

    dd:last-child {
      border-bottom: none;
    }
  }

  .log-content {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8px;

    .log-status {
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 12px;
      color: #fff;
    }

    .log-arrow {
      color: #999;
      font-size: 16px;
    }

    .log-operator,
    .log-remark {
      margin-left: 8px;
      font-size: 12px;
      color: #999;
    }
  }
}
</style>