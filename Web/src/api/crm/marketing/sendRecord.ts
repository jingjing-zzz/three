import request from '@/config/axios'

export interface MarketingSendRecordVO {
  id: number
  batchId: number
  campaignName: string
  batchName: string
  type: number
  target: string
  content: string
  status: number
  errorMessage: string
  sendTime: Date
  createTime: Date
}

export const getMarketingSendRecordPage = async (params) => {
  return await request.get({ url: `/crm/marketing/send-record/page`, params })
}

export const getMarketingSendRecord = async (id: number) => {
  return await request.get({ url: `/crm/marketing/send-record/get?id=` + id })
}