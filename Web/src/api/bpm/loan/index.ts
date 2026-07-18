import request from '@/config/axios'

export type LoanVO = {
  id: number
  status: number
  amount: number
  reason: string
  repaymentDate: string
  processInstanceId: string
  createTime: string
}

export const createLoan = async (data: LoanVO) => {
  return await request.post({ url: '/bpm/oa/loan/create', data })
}

export const getLoan = async (id: number) => {
  return await request.get({ url: '/bpm/oa/loan/get?id=' + id })
}

export const getLoanPage = async (params: PageParam) => {
  return await request.get({ url: '/bpm/oa/loan/page', params })
}