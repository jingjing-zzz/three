import request from '@/config/axios'

export interface MarketingEmailSendDirectReqVO {
  emails: string[]
  subject: string
  content: string
  campaignName?: string
  templateName?: string
}

export interface MarketingEmailSendDirectRespVO {
  batchId: number
  totalCount: number
  successCount: number
  failCount: number
  failedEmails: string[]
}

// 快速发送邮件（不预先创建批次，发送后自动生成一条已完成的批次记录）
export const sendDirectEmail = async (data: MarketingEmailSendDirectReqVO) => {
  return await request.post({ url: `/crm/marketing/email-batch/send-direct`, data })
}
