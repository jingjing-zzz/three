import request from '@/config/axios'

export interface MarketingCampaignVO {
  id: number
  name: string
  type: number
  status: number
  startTime: Date
  endTime: Date
  targetFilter: string
  description: string
  totalTargetCount: number
  sendCount: number
  successCount: number
  failCount: number
  createTime: Date
}

export const getMarketingCampaignPage = async (params) => {
  return await request.get({ url: `/crm/marketing/campaign/page`, params })
}

export const getMarketingCampaign = async (id: number) => {
  return await request.get({ url: `/crm/marketing/campaign/get?id=` + id })
}

export const createMarketingCampaign = async (data: MarketingCampaignVO) => {
  return await request.post({ url: `/crm/marketing/campaign/create`, data })
}

export const updateMarketingCampaign = async (data: MarketingCampaignVO) => {
  return await request.put({ url: `/crm/marketing/campaign/update`, data })
}

export const deleteMarketingCampaign = async (id: number) => {
  return await request.delete({ url: `/crm/marketing/campaign/delete?id=` + id })
}