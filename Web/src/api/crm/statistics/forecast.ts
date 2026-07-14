import request from '@/config/axios'

export interface ForecastSummaryVO {
  businessCount: number
  totalAmount: number
  forecastAmount: number
  wonAmount: number
  wonCount: number
  avgWinRate: number
}

export interface ForecastDetailVO {
  businessId: number
  businessName: string
  ownerUserName: string
  customerName: string
  statusName: string
  stagePercent: number
  totalPrice: number
  forecastAmount: number
  dealTime: string
  endStatus: number
}

export const ForecastApi = {
  getForecastSummary: (params: any) => {
    return request.get({ url: '/crm/statistics-forecast/summary', params })
  },
  getForecastPage: (params: any) => {
    return request.get({ url: '/crm/statistics-forecast/page', params })
  }
}
