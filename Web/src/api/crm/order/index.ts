import request from '@/config/axios'

export interface OrderVO {
  id: number
  no: string
  status: number
  customerId: number
  customerName?: string
  contractId?: number
  contractName?: string
  ownerUserId: number
  ownerUserName?: string
  orderTime: Date
  totalProductPrice: number
  discountPercent: number
  totalPrice: number
  remark: string
  createTime?: Date
  creator: string
  creatorName?: string
  products?: [
    {
      id: number
      productId: number
      productName: string
      productNo: string
      productUnit: string
      productPrice: number
      count: number
      totalPrice: number
      remark: string
    }
  ]
}

export interface OrderSaveReqVO {
  id?: number
  customerId: number
  contractId?: number
  ownerUserId: number
  orderTime: Date
  discountPercent: number
  remark: string
  products: [
    {
      id?: number
      productId: number
      productPrice: number
      count: number
      taxPercent?: number
      taxPrice?: number
      remark?: string
    }
  ]
}

export const getOrderPage = async (params) => {
  return await request.get({ url: `/crm/order/page`, params })
}

export const getOrderPageByCustomer = async (params: any) => {
  return await request.get({ url: `/crm/order/page-by-customer`, params })
}

export const getOrderPageByContract = async (params: any) => {
  return await request.get({ url: `/crm/order/page-by-contract`, params })
}

export const getOrder = async (id: number) => {
  return await request.get({ url: `/crm/order/get?id=` + id })
}

export const getOrderSimpleList = async (customerId: number) => {
  return await request.get({
    url: `/crm/order/simple-list?customerId=${customerId}`
  })
}

export const createOrder = async (data: OrderSaveReqVO) => {
  return await request.post({ url: `/crm/order/create`, data })
}

export const updateOrder = async (data: OrderSaveReqVO) => {
  return await request.put({ url: `/crm/order/update`, data })
}

export const deleteOrder = async (id: number) => {
  return await request.delete({ url: `/crm/order/delete?id=` + id })
}

export const startApproval = async (id: number) => {
  return await request.post({ url: `/crm/order/approval?id=` + id })
}

export const submitOrder = async (id: number) => {
  return await request.post({ url: `/crm/order/submit?id=` + id })
}
// 完成订单
export const completeOrder = async (id: number) => {
  return await request.post({ url: `/crm/order/complete?id=` + id })
}

// 获取订单统计
export const getOrderStatistics = async () => {
  return await request.get({ url: `/crm/order/statistics` })
}

export const exportOrder = async (params) => {
  return await request.download({ url: `/crm/order/export-excel`, params })
}