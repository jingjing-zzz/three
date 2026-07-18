import request from '@/config/axios'

export interface WorkOrderVO {
  id: number
  title: string
  content: string
  type: number
  typeName?: string
  priority: number
  priorityName?: string
  status: number
  statusName?: string
  customerId: number
  customerName?: string
  ownerUserId: number
  ownerUserName?: string
  processTime?: Date
  completeTime?: Date
  result?: string
  rejectReason?: string
  creator?: string
  creatorName?: string
  createTime?: Date
  updateTime?: Date
  logs?: Array<{
    statusBefore: number
    statusBeforeName?: string
    statusAfter: number
    statusAfterName?: string
    operatorName?: string
    createTime?: Date
    remark?: string
  }>
}

export const getWorkOrderPage = async (params) => {
  return await request.get({ url: '/crm/work-order/page', params })
}

export const getWorkOrder = async (id: number) => {
  return await request.get({ url: '/crm/work-order/get?id=' + id })
}

export const createWorkOrder = async (data: WorkOrderVO) => {
  return await request.post({ url: '/crm/work-order/create', data })
}

export const updateWorkOrder = async (data: WorkOrderVO) => {
  return await request.put({ url: '/crm/work-order/update', data })
}

export const updateWorkOrderStatus = async (data: {
  id: number
  status: number
  result?: string
  rejectReason?: string
  remark?: string
}) => {
  return await request.put({ url: '/crm/work-order/update-status', data })
}

export const deleteWorkOrder = async (id: number) => {
  return await request.delete({ url: '/crm/work-order/delete?id=' + id })
}

export const getWorkOrderStatistics = async () => {
  return await request.get({ url: '/crm/work-order/statistics' })
}

export const exportWorkOrder = async (params: any) => {
  return await request.download({ url: '/crm/work-order/export-excel', params })
}

export const WORK_ORDER_TYPE_OPTIONS = [
  { value: 1, label: '咨询' },
  { value: 2, label: '投诉' },
  { value: 3, label: '建议' },
  { value: 4, label: '问题' }
]

export const WORK_ORDER_PRIORITY_OPTIONS = [
  { value: 1, label: '紧急' },
  { value: 2, label: '高' },
  { value: 3, label: '中' },
  { value: 4, label: '低' }
]

export const WORK_ORDER_STATUS_OPTIONS = [
  { value: 1, label: '待处理' },
  { value: 2, label: '处理中' },
  { value: 3, label: '已完结' },
  { value: 4, label: '已退回' }
]