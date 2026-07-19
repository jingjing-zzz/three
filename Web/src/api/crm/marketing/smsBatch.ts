import request from '@/config/axios'

export interface MarketingSmsBatchVO {
  id: number
  campaignId: number
  campaignName: string
  templateId: number
  templateName: string
  content: string
  totalCount: number
  sendCount: number
  successCount: number
  failCount: number
  status: number
  createTime: Date
}

export const getMarketingSmsBatchPage = async (params) => {
  return await request.get({ url: `/crm/marketing/sms-batch/page`, params })
}

export const getMarketingSmsBatch = async (id: number) => {
  return await request.get({ url: `/crm/marketing/sms-batch/get?id=` + id })
}

export const createMarketingSmsBatch = async (data: MarketingSmsBatchVO) => {
  return await request.post({ url: `/crm/marketing/sms-batch/create`, data })
}

export const updateMarketingSmsBatch = async (data: MarketingSmsBatchVO) => {
  return await request.put({ url: `/crm/marketing/sms-batch/update`, data })
}

export const deleteMarketingSmsBatch = async (id: number) => {
  return await request.delete({ url: `/crm/marketing/sms-batch/delete?id=` + id })
}

// 发送短信批次
export const sendSmsBatch = async (id: number) => {
  return await request.put({ url: `/crm/marketing/sms-batch/send?id=` + id })
}