import request from '@/config/axios'

export type BusinessTripVO = {
  id: number
  status: number
  destination: string
  startTime: string
  endTime: string
  day: number
  reason: string
  processInstanceId: string
  createTime: string
}

export const createBusinessTrip = async (data: BusinessTripVO) => {
  return await request.post({ url: '/bpm/oa/business-trip/create', data })
}

export const getBusinessTrip = async (id: number) => {
  return await request.get({ url: '/bpm/oa/business-trip/get?id=' + id })
}

export const getBusinessTripPage = async (params: PageParam) => {
  return await request.get({ url: '/bpm/oa/business-trip/page', params })
}