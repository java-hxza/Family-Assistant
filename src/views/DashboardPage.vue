<template>
  <div class="dashboard">
    <!-- 总览卡片 -->
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="total-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>本月支出</span>
              <el-tag type="danger" size="large">¥{{ monthlyExpense.toFixed(2) }}</el-tag>
            </div>
          </template>
          <div class="card-trend">
            <span>较上月</span>
            <span :class="monthlyExpenseChange >= 0 ? 'increase' : 'decrease'">
              {{ monthlyExpenseChange >= 0 ? '+' : '' }}{{ monthlyExpenseChange.toFixed(2) }}%
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="total-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>本月收入</span>
              <el-tag type="success" size="large">¥{{ monthlyIncome.toFixed(2) }}</el-tag>
            </div>
          </template>
          <div class="card-trend">
            <span>较上月</span>
            <span :class="monthlyIncomeChange >= 0 ? 'increase' : 'decrease'">
              {{ monthlyIncomeChange >= 0 ? '+' : '' }}{{ monthlyIncomeChange.toFixed(2) }}%
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="total-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>本月结余</span>
              <el-tag :type="monthlyBalance >= 0 ? 'success' : 'danger'" size="large">
                ¥{{ monthlyBalance.toFixed(2) }}
              </el-tag>
            </div>
          </template>
          <div class="card-trend">
            <span>较上月</span>
            <span :class="monthlyBalanceChange >= 0 ? 'increase' : 'decrease'">
              {{ monthlyBalanceChange >= 0 ? '+' : '' }}{{ monthlyBalanceChange.toFixed(2) }}%
            </span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 年度账单 -->
    <el-row :gutter="20" class="mt-4">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>年度账单</span>
              <el-select v-model="selectedYear" placeholder="选择年份">
                <el-option
                  v-for="year in availableYears"
                  :key="year"
                  :label="year + '年'"
                  :value="year"
                />
              </el-select>
            </div>
          </template>
          <div ref="yearlyChartRef" class="yearly-chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 月度账单 -->
    <el-row :gutter="20" class="mt-4">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>支出统计</span>
            </div>
          </template>
          <Statistics 
            :expense-data="expenseData"
            :income-data="incomeData"
            :loading="loading"
            type="expense"
          />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>收入统计</span>
            </div>
          </template>
          <Statistics 
            :expense-data="expenseData"
            :income-data="incomeData"
            :loading="loading"
            type="income"
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近交易记录 -->
    <el-row :gutter="20" class="mt-4">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近交易记录</span>
            </div>
          </template>
          <RecentTransactions 
            :transactions="transactions"
            :loading="loading"
            :total="transactionsTotal"
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useStorage } from '@vueuse/core'
import * as echarts from 'echarts'
import Statistics from '../components/Statistics.vue'
import RecentTransactions from '../components/RecentTransactions.vue'
import { getExpenseList, getExpensesByDateRange } from '../api/expense'
import { getIncomeList, getIncomesByDateRange } from '../api/income'
import { ElMessage } from 'element-plus'
import { getMonthlyStatistics, getRecentTransactions } from '../api/statistics'
import request from '../utils/request'

const loading = ref(false)
const expenseData = ref([])
const incomeData = ref([])
const expenses = ref([])
const incomes = ref([])
const yearlyChartRef = ref(null)
let yearlyChart = null

const selectedYear = ref(new Date().getFullYear())
const availableYears = computed(() => {
  const years = new Set()
  ;[...expenses.value, ...incomes.value].forEach(item => {
    years.add(new Date(item.date).getFullYear())
  })
  return Array.from(years).sort()
})

const statistics = ref({
  monthlyExpense: 0,
  monthlyIncome: 0,
  monthlyBalance: 0,
  expenseChange: 0,
  incomeChange: 0,
  balanceChange: 0,
  expenseByCategory: {},
  incomeByCategory: {}
})

const transactions = ref([])
const transactionsTotal = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const loadData = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const [expenseRes, incomeRes, statsRes] = await Promise.all([
      getExpenseList(),
      getIncomeList(),
      getMonthlyStatistics()
    ])
    expenseData.value = expenseRes.data?.records || []
    incomeData.value = incomeRes.data?.records || []
    expenses.value = expenseRes.data?.records || []
    incomes.value = incomeRes.data?.records || []
    
    if (statsRes.data) {
      statistics.value = {
        ...statistics.value,
        ...statsRes.data,
        expenseByCategory: statsRes.data.expenseByCategory || {},
        incomeByCategory: statsRes.data.incomeByCategory || {}
      }
    }
    
    updateYearlyChart()
  } catch (error) {
    console.error('Failed to load data:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const loadDataByDateRange = async () => {
  const startDate = `${selectedYear.value}-01-01 00:00:00`
  const endDate = `${selectedYear.value}-12-31 23:59:59`
  
  try {
    const [expensesRes, incomesRes] = await Promise.all([
      getExpensesByDateRange(startDate, endDate),
      getIncomesByDateRange(startDate, endDate)
    ])
    expenses.value = expensesRes.data?.records || []
    incomes.value = incomesRes.data?.records || []
  } catch (error) {
    ElMessage.error('获取数据失败')
  }
}

const loadStatistics = async () => {
  try {
    const res = await getMonthlyStatistics()
    statistics.value = res.data
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

// 计算月度数据
const monthlyData = computed(() => {
  const currentDate = new Date()
  const currentYear = currentDate.getFullYear()
  const currentMonth = currentDate.getMonth()
  const lastMonth = currentMonth === 0 ? 11 : currentMonth - 1
  const lastMonthYear = currentMonth === 0 ? currentYear - 1 : currentYear

  const getCurrentMonthData = (items) => {
    return items.filter(item => {
      const date = new Date(item.expenseDate || item.incomeDate)
      return date.getFullYear() === currentYear && date.getMonth() === currentMonth
    })
  }

  const getLastMonthData = (items) => {
    return items.filter(item => {
      const date = new Date(item.expenseDate || item.incomeDate)
      return date.getFullYear() === lastMonthYear && date.getMonth() === lastMonth
    })
  }

  const currentMonthExpenses = getCurrentMonthData(expenses.value)
  const currentMonthIncomes = getCurrentMonthData(incomes.value)
  const lastMonthExpenses = getLastMonthData(expenses.value)
  const lastMonthIncomes = getLastMonthData(incomes.value)

  return {
    currentMonthExpenses,
    currentMonthIncomes,
    lastMonthExpenses,
    lastMonthIncomes
  }
})

// 计算各种统计数据
const monthlyExpense = computed(() => statistics.value.monthlyExpense)
const monthlyIncome = computed(() => statistics.value.monthlyIncome)
const monthlyBalance = computed(() => statistics.value.monthlyBalance)
const monthlyExpenseChange = computed(() => statistics.value.expenseChange)
const monthlyIncomeChange = computed(() => statistics.value.incomeChange)
const monthlyBalanceChange = computed(() => statistics.value.balanceChange)

// 初始化年度图表
const initYearlyChart = () => {
  if (!yearlyChartRef.value) return
  
  yearlyChart = echarts.init(yearlyChartRef.value)
  updateYearlyChart()
}

const updateYearlyChart = () => {
  if (!yearlyChart) return

  const monthlyData = Array(12).fill(0).map((_, index) => {
    const monthExpenses = expenses.value.filter(expense => {
      const date = new Date(expense.expenseDate)
      return date.getFullYear() === selectedYear.value && date.getMonth() === index
    }).reduce((sum, exp) => sum + Number(exp.amount), 0)

    const monthIncomes = incomes.value.filter(income => {
      const date = new Date(income.incomeDate)
      return date.getFullYear() === selectedYear.value && date.getMonth() === index
    }).reduce((sum, inc) => sum + Number(inc.amount), 0)

    return {
      month: index + 1,
      expenses: monthExpenses,
      incomes: monthIncomes,
      balance: monthIncomes - monthExpenses
    }
  })

  yearlyChart.setOption({
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        return `${params[0].name}<br/>
                收入: ¥${params[0].value.toFixed(2)}<br/>
                支出: ¥${params[1].value.toFixed(2)}<br/>
                结余: ¥${params[2].value.toFixed(2)}`
      }
    },
    legend: {
      data: ['收入', '支出', '结余']
    },
    xAxis: {
      type: 'category',
      data: monthlyData.map(d => `${d.month}月`)
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: (value) => `¥${value}`
      }
    },
    series: [
      {
        name: '收入',
        type: 'bar',
        data: monthlyData.map(d => d.incomes),
        itemStyle: { color: '#67c23a' }
      },
      {
        name: '支出',
        type: 'bar',
        data: monthlyData.map(d => d.expenses),
        itemStyle: { color: '#f56c6c' }
      },
      {
        name: '结余',
        type: 'line',
        data: monthlyData.map(d => d.balance),
        itemStyle: { color: '#409EFF' }
      }
    ]
  })
}

const loadTransactions = async () => {
  try {
    const res = await getRecentTransactions({
      current: currentPage.value,
      size: pageSize.value
    })
    transactions.value = res.data.records || []
    transactionsTotal.value = res.data.total || 0
  } catch (error) {
    console.error('Failed to load transactions:', error)
    ElMessage.error('加载交易记录失败')
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadTransactions()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadTransactions()
}

const handleResize = () => {
  yearlyChart?.resize()
}

onMounted(() => {
  loadStatistics()
  loadData()
  initYearlyChart()
  loadTransactions()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  yearlyChart?.dispose()
})

watch(selectedYear, () => {
  loadDataByDateRange()
  updateYearlyChart()
})

// 监听数据变化
watch([expenses, incomes], () => {
  updateYearlyChart()
}, { deep: true })

// 监听分页变化
watch([currentPage, pageSize], () => {
  loadTransactions()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.mt-4 {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-card {
  transition: transform 0.3s ease;
}

.total-card:hover {
  transform: translateY(-5px);
}

.card-trend {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  color: #909399;
}

.increase {
  color: #67c23a;
}

.decrease {
  color: #f56c6c;
}

.yearly-chart {
  height: 400px;
  margin-top: 20px;
}

/* 添加响应式布局 */
@media (max-width: 768px) {
  .el-row {
    margin-left: 0 !important;
    margin-right: 0 !important;
  }

  .el-col {
    padding-left: 0 !important;
    padding-right: 0 !important;
  }
}
</style> 