import request from '@/config/axios'

export type CustomerVisitVO = {
  id: number
  status: number
  customerName: string
  customerContact: string
  contactPhone: string
  visitTime: string
  visitLocation: string
  purpose: string
  processInstanceId: string
  createTime: string
}

export const createCustomerVisit = async (data: CustomerVisitVO) => {
  return await request.post({ url: '/bpm/oa/customer-visit/create', data })
}

export const getCustomerVisit = async (id: number) => {
  return await request.get({ url: '/bpm/oa/customer-visit/get?id=' + id })
}

export const getCustomerVisitPage = async (params: PageParam) => {
  return await request.get({ url: '/bpm/oa/customer-visit/page', params })
}