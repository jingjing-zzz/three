import request from '@/config/axios'

export interface WonReqVO {
  beginDate: string
  endDate: string
  userIds?: number[]
  customerIds?: number[]
  sources?: string[]
}

export interface WonSummaryVO {
  wonCount: number
  totalAmount: number
  avgAmount: number
  maxAmount: number
  minAmount: number
  newCustomerCount: number
  oldCustomerCount: number
}

export interface WonDetailVO {
  businessName: string
  customerName: string
  sourceName: string
  competitor: string
  ownerUserName: string
  totalPrice: number
  totalProductPrice: number
  discountPercent: number
  dealTime: Date
  createTime: Date
  dealDays: number
}

export const getWonSummary = (params: WonReqVO) => {
  return request.get({ url: '/crm/statistics-won/summary', params })
}

export const getWonDetail = (params: WonReqVO) => {
  return request.get({ url: '/crm/statistics-won/detail', params })
}

export const exportWonExcel = (params: WonReqVO) => {
  return request.get({ url: '/crm/statistics-won/export-excel', params, responseType: 'blob' })
}
