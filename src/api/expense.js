import request from '../utils/request'

export function getExpenseList(params) {
  return request({
    url: '/api/expense/list',
    method: 'get',
    params: {
      current: params?.current || 1,
      size: params?.size || 10,
      startDate: params?.startDate,
      endDate: params?.endDate,
      category: params?.category
    }
  })
}

export function addExpense(data) {
  return request({
    url: '/api/expense',
    method: 'post',
    data
  })
}

export function updateExpense(id, data) {
  return request({
    url: `/api/expense/${id}`,
    method: 'put',
    data
  })
}

export function deleteExpense(id) {
  return request({
    url: `/api/expense/${id}`,
    method: 'delete'
  })
}

export function getExpensesByDateRange(startDate, endDate) {
  return request({
    url: '/api/expense/range',
    method: 'get',
    params: { startDate, endDate }
  })
} 