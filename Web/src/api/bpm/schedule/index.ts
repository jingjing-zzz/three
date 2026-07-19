import request from '@/config/axios'

export type ScheduleVO = {
  id: number
  status: number
  title: string
  description: string
  startTime: string
  endTime: string
  location: string
  remindTime: string
  createTime: string
}

export const createSchedule = async (data: ScheduleVO) => {
  return await request.post({ url: '/bpm/oa/schedule/create', data })
}

export const updateSchedule = async (data: ScheduleVO) => {
  return await request.put({ url: '/bpm/oa/schedule/update', data })
}

export const deleteSchedule = async (id: number) => {
  return await request.delete({ url: '/bpm/oa/schedule/delete?id=' + id })
}

export const getSchedule = async (id: number) => {
  return await request.get({ url: '/bpm/oa/schedule/get?id=' + id })
}

export const getSchedulePage = async (params: PageParam) => {
  return await request.get({ url: '/bpm/oa/schedule/page', params })
}