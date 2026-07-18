import request from '@/config/axios'
import { BpmProcessInstanceStatus } from '@/utils/constants'

export const TaskStatusEnum = BpmProcessInstanceStatus

export type TaskVO = {
  id: number
  status: number
  priority: number
  title: string
  description: string
  assigneeId: number
  startTime: string
  endTime: string
  createTime: string
}

export const createTask = async (data: TaskVO) => {
  return await request.post({ url: '/bpm/oa/task/create', data })
}

export const updateTask = async (data: TaskVO) => {
  return await request.put({ url: '/bpm/oa/task/update', data })
}

export const deleteTask = async (id: number) => {
  return await request.delete({ url: '/bpm/oa/task/delete?id=' + id })
}

export const getTask = async (id: number) => {
  return await request.get({ url: '/bpm/oa/task/get?id=' + id })
}

export const getTaskPage = async (params: PageParam) => {
  return await request.get({ url: '/bpm/oa/task/page', params })
}

export const getTaskTodoPage = async (params: any) => {
  return await request.get({ url: '/bpm/task/todo-page', params })
}

export const getTaskDonePage = async (params: any) => {
  return await request.get({ url: '/bpm/task/done-page', params })
}

export const approveTask = async (data: any) => {
  return await request.put({ url: '/bpm/task/approve', data })
}

export const rejectTask = async (data: any) => {
  return await request.put({ url: '/bpm/task/reject', data })
}

export const copyTask = async (data: any) => {
  return await request.put({ url: '/bpm/task/copy', data })
}

export const transferTask = async (data: any) => {
  return await request.put({ url: '/bpm/task/transfer', data })
}

export const delegateTask = async (data: any) => {
  return await request.put({ url: '/bpm/task/delegate', data })
}

export const signCreateTask = async (data: any) => {
  return await request.put({ url: '/bpm/task/create-sign', data })
}

export const signDeleteTask = async (data: any) => {
  return await request.delete({ url: '/bpm/task/delete-sign', data })
}

export const returnTask = async (data: any) => {
  return await request.put({ url: '/bpm/task/return', data })
}

export const getTaskListByReturn = async (taskId: string) => {
  return await request.get({ url: '/bpm/task/list-by-return', params: { id: taskId } })
}

export const getTaskListByProcessInstanceId = async (processInstanceId: string) => {
  return await request.get({ url: '/bpm/task/list-by-process-instance-id', params: { processInstanceId } })
}

