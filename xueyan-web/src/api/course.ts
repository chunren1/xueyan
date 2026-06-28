import request from './request'

export interface CourseInfo {
  id: number
  name: string
  description: string
  coverUrl: string
  price: number
  stock: number
  category: string
  teacherName: string
  status: number
  createdAt: string
}

export interface CourseListParams {
  category?: string
  keyword?: string
  page?: number
  size?: number
}

export const courseApi = {
  list(params?: CourseListParams) {
    return request.get<{ data: CourseInfo[] }>('/course/list', { params })
  },
  detail(id: number) {
    return request.get<{ data: CourseInfo }>(`/course/${id}`)
  },
}
