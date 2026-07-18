import request from '@/config/axios'

export interface MarketingCustomerCareVO {
  id: number
  name: string
  type: number
  triggerType: number
  triggerCondition: string
  sendChannel: number
  templateId: number
  templateName: string
  content: string
  subject: string
  status: number
  remark: string
  createTime: Date
}

export const getMarketingCustomerCarePage = async (params) => {
  return await request.get({ url: `/crm/marketing/customer-care/page`, params })
}

export const getMarketingCustomerCare = async (id: number) => {
  return await request.get({ url: `/crm/marketing/customer-care/get?id=` + id })
}

export const createMarketingCustomerCare = async (data: MarketingCustomerCareVO) => {
  return await request.post({ url: `/crm/marketing/customer-care/create`, data })
}

export const updateMarketingCustomerCare = async (data: MarketingCustomerCareVO) => {
  return await request.put({ url: `/crm/marketing/customer-care/update`, data })
}

export const deleteMarketingCustomerCare = async (id: number) => {
  return await request.delete({ url: `/crm/marketing/customer-care/delete?id=` + id })
}