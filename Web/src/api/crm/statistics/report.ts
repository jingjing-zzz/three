import request from '@/config/axios'

/** 通用分布项 */
export interface ReportItem {
  name: string
  value: number
  amount: number
}

/** 阶段分布项 */
export interface ReportStageItem {
  name: string
  percent: number
  value: number
  amount: number
}

/** 负责人业绩项 */
export interface ReportOwnerItem {
  userId: number
  userName: string
  businessCount: number
  totalAmount: number
  wonAmount: number
  forecastAmount: number
}

/** 客户分布项 */
export interface ReportCustomerItem {
  customerId: number
  customerName: string
  businessCount: number
  totalAmount: number
}

/** 报表总览响应 */
export interface ReportOverviewVO {
  statusDistribution: ReportItem[]
  stageDistribution: ReportStageItem[]
  sourceDistribution: ReportItem[]
  ownerPerformance: ReportOwnerItem[]
  customerDistribution: ReportCustomerItem[]
  totalBusinessCount: number
  totalAmount: number
  wonAmount: number
  forecastAmount: number
}

/** 报表查询参数 */
export interface ReportReqVO {
  userId?: number
  times?: string[]
  includeLost?: boolean
}

export const ReportApi = {
  /** 获取商机报表总览数据（支持按负责人、预计成交时间筛选，默认排除已流失） */
  getReportOverview: (params?: ReportReqVO) => {
    return request.get({ url: '/crm/statistics-report/overview', params })
  }
}
