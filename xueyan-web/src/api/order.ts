import request from './request'

export interface CreateOrderParams {
  userId: number
  courseId: number
  quantity: number
}

export interface OrderInfo {
  id: number
  orderNo: string
  userId: number
  courseId: number
  courseName: string
  amount: number
  status: 'WAIT_PAY' | 'PAID' | 'COMPLETED' | 'CANCELLED'
  createdAt: string
}

export const orderApi = {
  create(data: CreateOrderParams) {
    return request.post<{ data: OrderInfo }>('/order/create', data)
  },
  getByNo(orderNo: string) {
    return request.get<{ data: OrderInfo }>(`/order/${orderNo}`)
  },
}
