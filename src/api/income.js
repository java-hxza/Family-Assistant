import request from '../utils/request'

export function getIncomeList(params) {
  return request({
    url: '/api/income/list',
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

export function addIncome(data) {
  return request({
    url: '/api/income',
    method: 'post',
    data
  })
}

export function updateIncome(id, data) {
  return request({
    url: `/api/income/${id}`,
    method: 'put',
    data
  })
}

export function deleteIncome(id) {
  return request({
    url: `/api/income/${id}`,
    method: 'delete'
  })
}

export function getIncomesByDateRange(startDate, endDate) {
  return request({
    url: '/api/income/range',
    method: 'get',
    params: { startDate, endDate }
  })
} 