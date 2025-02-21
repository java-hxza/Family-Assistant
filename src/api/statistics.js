import request from '../utils/request'

// 获取月度统计数据
export function getMonthlyStatistics() {
  return request({
    url: '/api/statistics/monthly',
    method: 'get'
  })
}

// 获取年度统计数据
export function getYearlyStatistics(year) {
  return request({
    url: '/api/statistics/yearly',
    method: 'get',
    params: { year }
  })
}

// 获取支出分类统计
export function getExpenseCategories() {
  return request({
    url: '/api/statistics/expense-categories',
    method: 'get'
  })
}

// 获取收入分类统计
export function getIncomeCategories() {
  return request({
    url: '/api/statistics/income-categories',
    method: 'get'
  })
}

// 获取日期范围内的支出
export function getExpensesByDateRange(startDate, endDate) {
  return request({
    url: '/api/expense/range',
    method: 'get',
    params: { startDate, endDate }
  })
}

// 获取日期范围内的收入
export function getIncomesByDateRange(startDate, endDate) {
  return request({
    url: '/api/income/range',
    method: 'get',
    params: { startDate, endDate }
  })
}

export function getRecentTransactions(params) {
  return request({
    url: '/api/statistics/recent-transactions',
    method: 'get',
    params: {
      current: params?.current || 1,
      size: params?.size || 10
    }
  })
} 