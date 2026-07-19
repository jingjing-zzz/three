import request from '@/config/axios'

export interface WorkOrderVO {
  id: number | undefined
  orderNo: string
  title: string
  type: number
  typeName: string
  priority: number
  priorityName: string
  status: number
  statusName: string
  content: string
  reporterId: number
  reporterName: string
  assigneeId: number
  assigneeName: string
  deptId: number
  deptName: string
  customerId: number
  customerName: string
  handleNote: string
  createTime: Date
}

export interface WorkOrderPageReqVO extends PageParam {
  orderNo?: string
  title?: string
  type?: number
  priority?: number
  status?: number
  assigneeName?: string
  createTime?: Date[]
}

export interface WorkOrderStatusFlowVO {
  id: number
  orderId: number
  fromStatus: number
  fromStatusName: string
  toStatus: number
  toStatusName: string
  operatorName: string
  remark: string
  createTime: Date
}

export interface WorkOrderTypeVO {
  id: number
  name: string
  code: string
  status: number
  remark: string
}

export interface EnumVO {
  value: number
  label: string
}

export interface WorkOrderStatisticsVO {
  total: number
  pending: number
  processing: number
  completed: number
  rejected: number
  statusDistribution: Array<{ status: number; statusName: string; count: number; percentage: number }>
  typeDistribution: Array<{ type: number; typeName: string; count: number }>
  priorityDistribution: Array<{ priority: number; priorityName: string; count: number }>
  trendData: Array<{ date: string; count: number }>
}

export const getWorkOrderPage = (params: WorkOrderPageReqVO) => {
  return request.get({ url: '/system/work-order/page', params })
}

export const getWorkOrder = (id: number) => {
  return request.get({ url: '/system/work-order/get?id=' + id })
}

export const createWorkOrder = (data: WorkOrderVO) => {
  return request.post({ url: '/system/work-order/create', data })
}

export const updateWorkOrder = (data: WorkOrderVO) => {
  return request.put({ url: '/system/work-order/update', data })
}

export const deleteWorkOrder = (id: number) => {
  return request.delete({ url: '/system/work-order/delete?id=' + id })
}

export const deleteWorkOrderList = (ids: number[]) => {
  return request.delete({ url: '/system/work-order/delete-list', params: { ids: ids.join(',') } })
}

export const processWorkOrder = (data: { id: number; assignee?: string; remark?: string }) => {
  return request.put({ url: '/system/work-order/status/process', data })
}

export const completeWorkOrder = (data: { id: number; processResult?: string; remark?: string }) => {
  return request.put({ url: '/system/work-order/status/complete', data })
}

export const rejectWorkOrder = (data: { id: number; remark?: string }) => {
  return request.put({ url: '/system/work-order/status/reject', data })
}

export const getWorkOrderStatusFlow = (orderId: number) => {
  return request.get({ url: '/system/work-order/status-flow?orderId=' + orderId })
}

export const getWorkOrderTypeList = () => {
  return request.get({ url: '/system/work-order/type/list' })
}

export const getWorkOrderStatusEnum = () => {
  return request.get({ url: '/system/work-order/status/enum' })
}

export const getWorkOrderPriorityEnum = () => {
  return request.get({ url: '/system/work-order/priority/enum' })
}

export const getWorkOrderTypeEnum = () => {
  return request.get({ url: '/system/work-order/type/enum' })
}

export interface WorkOrderStatisticsReqVO {
  orderNo?: string
  title?: string
  type?: number
  priority?: number
  reporterId?: number
  assigneeId?: number
  deptId?: number
  createTimeBegin?: string
  createTimeEnd?: string
}

export const getWorkOrderStatistics = (params: WorkOrderStatisticsReqVO) => {
  return request.get({ url: '/system/work-order/statistics', params })
}

// 导出工单 Excel
export const exportWorkOrder = (params: WorkOrderPageReqVO) => {
  return request.download({ url: '/system/work-order/export-excel', params })
}
