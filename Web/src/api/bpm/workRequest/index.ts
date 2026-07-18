import request from '@/config/axios'

export type WorkRequestVO = {
  id: number
  status: number
  title: string
  content: string
  processInstanceId: string
  createTime: string
}

export const createWorkRequest = async (data: WorkRequestVO) => {
  return await request.post({ url: '/bpm/oa/work-request/create', data })
}

export const getWorkRequest = async (id: number) => {
  return await request.get({ url: '/bpm/oa/work-request/get?id=' + id })
}

export const getWorkRequestPage = async (params: PageParam) => {
  return await request.get({ url: '/bpm/oa/work-request/page', params })
}