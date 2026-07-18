import request from '@/config/axios'

export type WorkReportVO = {
  id: number
  status: number
  type: number
  title: string
  content: string
  reportDate: string
  reviewComment: string
  processInstanceId: string
  createTime: string
}

export const createWorkReport = async (data: WorkReportVO) => {
  return await request.post({ url: '/bpm/oa/work-report/create', data })
}

export const getWorkReport = async (id: number) => {
  return await request.get({ url: '/bpm/oa/work-report/get?id=' + id })
}

export const getWorkReportPage = async (params: PageParam) => {
  return await request.get({ url: '/bpm/oa/work-report/page', params })
}