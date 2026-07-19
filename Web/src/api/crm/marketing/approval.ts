import request from '@/config/axios'

export interface MarketingApprovalVO {
  id: number
  batchType: number
  batchId: number
  batchName: string
  status: number
  reason: string
  reviewerId: number
  reviewerName: string
  reviewTime: Date
  createTime: Date
}

export const getMarketingApprovalPage = async (params) => {
  return await request.get({ url: `/crm/marketing/approval/page`, params })
}

export const getMarketingApproval = async (id: number) => {
  return await request.get({ url: `/crm/marketing/approval/get?id=` + id })
}

export const createMarketingApproval = async (data: MarketingApprovalVO) => {
  return await request.post({ url: `/crm/marketing/approval/create`, data })
}

export const updateMarketingApproval = async (data: MarketingApprovalVO) => {
  return await request.put({ url: `/crm/marketing/approval/update`, data })
}

export const deleteMarketingApproval = async (id: number) => {
  return await request.delete({ url: `/crm/marketing/approval/delete?id=` + id })
}

export const approveMarketingApproval = async (id: number) => {
  return await request.put({ url: `/crm/marketing/approval/approve?id=` + id })
}

export const rejectMarketingApproval = async (id: number) => {
  return await request.put({ url: `/crm/marketing/approval/reject?id=` + id })
}