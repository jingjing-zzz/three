import request from '@/config/axios'

export interface QuotationVO {
  id?: number
  businessId: number
  quotationNo?: string
  status?: number
  statusName?: string
  totalProductPrice?: number
  discountPercent?: number
  totalPrice?: number
  confirmedBy?: number
  confirmedByName?: string
  confirmedTime?: Date
  remark?: string
  createTime?: Date
  items?: QuotationItemVO[]
}

export interface QuotationItemVO {
  id?: number
  productId: number
  productName: string
  productNo: string
  standardPrice: number
  actualPrice: number
  count: number
  discountPercent?: number
  totalPrice: number
  gift?: string
  remark?: string
}

// 创建报价草稿
export const createQuotationDraft = (businessId: number) => {
  return request.post({ url: `/crm/business-quotation/create-draft?businessId=${businessId}` })
}

// 确认报价
export const confirmQuotation = (quotationId: number) => {
  return request.put({ url: `/crm/business-quotation/confirm?quotationId=${quotationId}` })
}

// 作废报价
export const voidQuotation = (quotationId: number) => {
  return request.put({ url: `/crm/business-quotation/void?quotationId=${quotationId}` })
}

// 获取报价详情
export const getQuotation = (id: number) => {
  return request.get({ url: `/crm/business-quotation/get?id=${id}` })
}

// 获取报价分页
export const getQuotationPage = (params: any) => {
  return request.get({ url: '/crm/business-quotation/page', params })
}

// 获取商机最新已确认报价
export const getLatestConfirmedQuotation = (businessId: number) => {
  return request.get({ url: `/crm/business-quotation/latest-confirmed?businessId=${businessId}` })
}
