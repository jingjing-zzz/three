import request from '@/config/axios'

export interface FinanceRecordVO {
  id?: number
  no?: string
  type: number
  status?: number
  recordTime: Date | string
  dueTime?: Date | string
  overdue?: boolean
  applicantUserId?: number
  applicantUserName?: string
  financeUserId?: number
  financeUserName?: string
  accountId?: number
  accountName?: string
  subject: string
  counterparty?: string
  invoiceNo?: string
  amount: number
  taxAmount?: number
  totalAmount?: number
  processInstanceId?: string
  remark?: string
  creatorName?: string
  createTime?: Date | string
}

export interface FinanceSummaryVO {
  invoiceAmount: number
  reimbursementAmount: number
  refundAmount: number
  expenseAmount: number
  receiptAmount: number
  paymentAmount: number
  netAmount: number
  overdueCount: number
}

export const FinanceRecordApi = {
  getPage: async (params: any) => {
    return await request.get({ url: '/erp/finance-record/page', params })
  },
  getSummary: async (params: any) => {
    return await request.get({ url: '/erp/finance-record/summary', params })
  },
  get: async (id: number) => {
    return await request.get({ url: '/erp/finance-record/get?id=' + id })
  },
  create: async (data: FinanceRecordVO) => {
    return await request.post({ url: '/erp/finance-record/create', data })
  },
  update: async (data: FinanceRecordVO) => {
    return await request.put({ url: '/erp/finance-record/update', data })
  },
  updateStatus: async (id: number, status: number) => {
    return await request.put({ url: '/erp/finance-record/update-status', params: { id, status } })
  },
  markOverdue: async () => {
    return await request.put({ url: '/erp/finance-record/mark-overdue' })
  },
  delete: async (ids: number[]) => {
    return await request.delete({ url: '/erp/finance-record/delete', params: { ids: ids.join(',') } })
  },
  exportExcel: async (params: any) => {
    return await request.download({ url: '/erp/finance-record/export-excel', params })
  }
}
