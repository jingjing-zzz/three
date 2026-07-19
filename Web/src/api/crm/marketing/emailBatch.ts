import request from '@/config/axios'

export interface MarketingEmailBatchVO {
  id: number
  campaignId: number
  campaignName: string
  templateId: number
  templateName: string
  subject: string
  content: string
  totalCount: number
  sendCount: number
  successCount: number
  failCount: number
  status: number
  createTime: Date
}

export const getMarketingEmailBatchPage = async (params) => {
  return await request.get({ url: `/crm/marketing/email-batch/page`, params })
}

export const getMarketingEmailBatch = async (id: number) => {
  return await request.get({ url: `/crm/marketing/email-batch/get?id=` + id })
}

export const createMarketingEmailBatch = async (data: MarketingEmailBatchVO) => {
  return await request.post({ url: `/crm/marketing/email-batch/create`, data })
}

export const updateMarketingEmailBatch = async (data: MarketingEmailBatchVO) => {
  return await request.put({ url: `/crm/marketing/email-batch/update`, data })
}

export const deleteMarketingEmailBatch = async (id: number) => {
  return await request.delete({ url: `/crm/marketing/email-batch/delete?id=` + id })
}

// 发送邮件批次
export const sendEmailBatch = async (id: number) => {
  return await request.put({ url: `/crm/marketing/email-batch/send?id=` + id })
}