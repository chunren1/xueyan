import request from './request'

export interface RegisterParams {
  username: string
  password: string
  nickname?: string
}

export interface LoginParams {
  username: string
  password: string
}

export interface UserInfo {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  status: number
  createdAt: string
}

export interface LoginResult {
  userId: number
  username: string
  nickname: string
}

export const userApi = {
  register(data: RegisterParams) {
    return request.post<{ data: UserInfo }>('/user/register', data)
  },
  login(data: LoginParams) {
    return request.post<{ data: LoginResult }>('/user/login', data)
  },
  getById(id: number) {
    return request.get<{ data: UserInfo }>(`/user/${id}`)
  },
}
