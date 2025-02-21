<template>
  <div class="statistics">
    <!-- 月度统计卡片 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="8" v-for="(stat, index) in monthlyStats" :key="index">
        <el-card class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-header">
            <span class="stat-title">{{ stat.title }}</span>
            <el-tag :type="stat.type" size="large">
              ¥{{ formatNumber(stat.amount) }}
            </el-tag>
          </div>
          <div class="stat-trend">
            <span>较上月</span>
            <span :class="stat.change >= 0 ? 'increase' : 'decrease'">
              {{ stat.change >= 0 ? '+' : '' }}{{ stat.change.toFixed(2) }}%
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
              <el-select 
                v-model="selectedYear" 
                placeholder="选择年份"
                :disabled="loading"
              >
                <el-option
                  v-for="year in yearOptions"
                  :key="year"
                  :label="year + '年'"
                  :value="year"
                />
              </el-select>
            </div>
          </template>
          <div ref="yearlyChart" style="height: 400px">
            <el-empty v-if="!hasData" description="暂无数据" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 分类统计图表 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>支出分类</span>
            </div>
          </template>
          <div ref="pieChart" style="height: 300px" v-loading="loading">
            <el-empty v-if="!hasData" description="暂无数据" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>收入分类</span>
            </div>
          </template>
          <div ref="pieChart" style="height: 300px" v-loading="loading">
            <el-empty v-if="!hasData" description="暂无数据" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { 
  getMonthlyStatistics,
  getExpenseCategories,
  getIncomeCategories 
} from '../api/statistics'
import { getExpensesByDateRange } from '../api/expense'
import { getIncomesByDateRange } from '../api/income'

const props = defineProps({
  expenseData: {
    type: Array,
    default: () => []
  },
  incomeData: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  type: {
    type: String,
    default: 'expense',
    validator: (value) => ['expense', 'income'].includes(value)
  }
})

const yearlyChart = ref(null)
const pieChart = ref(null)
let yearlyChartInstance = null
let chartInstance = null

// 状态管理
const hasData = ref(false)

// 年份选择
const currentYear = new Date().getFullYear()
const selectedYear = ref(currentYear)
const yearOptions = computed(() => {
  const years = []
  for (let i = 0; i < 5; i++) {
    years.push(currentYear - i)
  }
  return years
})

// 月度统计数据
const monthlyData = ref({
  monthlyExpense: 0,
  monthlyIncome: 0,
  monthlyBalance: 0,
  expenseChange: 0,
  incomeChange: 0,
  balanceChange: 0
})

const monthlyStats = computed(() => [
  {
    title: '本月支出',
    amount: monthlyData.value.monthlyExpense,
    change: monthlyData.value.expenseChange,
    type: 'danger'
  },
  {
    title: '本月收入',
    amount: monthlyData.value.monthlyIncome,
    change: monthlyData.value.incomeChange,
    type: 'success'
  },
  {
    title: '本月结余',
    amount: monthlyData.value.monthlyBalance,
    change: monthlyData.value.balanceChange,
    type: monthlyData.value.monthlyBalance >= 0 ? 'success' : 'danger'
  }
])

// 加载数据
const loadMonthlyData = async () => {
  try {
    const res = await getMonthlyStatistics()
    monthlyData.value = res.data
  } catch (error) {
    console.error('Failed to load monthly statistics:', error)
  }
}

const loadYearlyData = async () => {
  if (props.loading) return
  props.loading = true
  hasData.value = false

  try {
    if (!selectedYear.value || isNaN(selectedYear.value)) {
      selectedYear.value = currentYear
    }
    
    const startDate = `${selectedYear.value}-01-01 00:00:00`
    const endDate = `${selectedYear.value}-12-31 23:59:59`
    
    const [expenseRes, incomeRes] = await Promise.all([
      getExpensesByDateRange(startDate, endDate),
      getIncomesByDateRange(startDate, endDate)
    ])

    // 处理月度数据
    const monthlyExpenses = new Array(12).fill(0)
    const monthlyIncomes = new Array(12).fill(0)

    if (expenseRes.data) {
      expenseRes.data.forEach(expense => {
        const date = new Date(expense.expenseDate)
        if (!isNaN(date.getTime())) {
          const month = date.getMonth()
          monthlyExpenses[month] = (monthlyExpenses[month] || 0) + Number(expense.amount)
        }
      })
    }

    if (incomeRes.data) {
      incomeRes.data.forEach(income => {
        const date = new Date(income.incomeDate)
        if (!isNaN(date.getTime())) {
          const month = date.getMonth()
          monthlyIncomes[month] = (monthlyIncomes[month] || 0) + Number(income.amount)
        }
      })
    }

    hasData.value = monthlyExpenses.some(amount => amount > 0) || 
                    monthlyIncomes.some(amount => amount > 0)

    if (hasData.value) {
      initYearlyChart({
        monthlyExpenses,
        monthlyIncomes
      })
    }
  } catch (error) {
    console.error('Failed to load yearly statistics:', error)
    ElMessage.error('加载年度数据失败')
  } finally {
    props.loading = false
  }
}

const loadCategoryData = async () => {
  try {
    if (props.type === 'expense') {
      const expenseRes = await getExpenseCategories()
      initPieChart(expenseRes.data)
    } else {
      const incomeRes = await getIncomeCategories()
      initPieChart(incomeRes.data)
    }
  } catch (error) {
    console.error('Failed to load category statistics:', error)
    ElMessage.error(`加载${props.type === 'income' ? '收入' : '支出'}分类统计失败`)
  }
}

// 初始化图表
const initYearlyChart = (data) => {
  if (!yearlyChartInstance) {
    yearlyChartInstance = echarts.init(yearlyChart.value)
  }

  const months = Array.from({ length: 12 }, (_, i) => i + 1 + '月')
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        return params[0].axisValue + '<br/>' +
          params.map(param => {
            return param.seriesName + ': ¥' + formatNumber(param.value)
          }).join('<br/>')
      }
    },
    legend: {
      data: ['支出', '收入'],
      top: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: months,
      boundaryGap: false
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: (value) => '¥' + formatNumber(value)
      }
    },
    series: [
      {
        name: '支出',
        type: 'line',
        data: data.monthlyExpenses,
        smooth: true,
        itemStyle: {
          color: '#F56C6C'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0,
              color: 'rgba(245, 108, 108, 0.3)'
            }, {
              offset: 1,
              color: 'rgba(245, 108, 108, 0.1)'
            }]
          }
        }
      },
      {
        name: '收入',
        type: 'line',
        data: data.monthlyIncomes,
        smooth: true,
        itemStyle: {
          color: '#67C23A'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0,
              color: 'rgba(103, 194, 58, 0.3)'
            }, {
              offset: 1,
              color: 'rgba(103, 194, 58, 0.1)'
            }]
          }
        }
      }
    ]
  }
  yearlyChartInstance.setOption(option)
}

const initPieChart = (data) => {
  if (!chartInstance) {
    chartInstance = echarts.init(pieChart.value)
  }

  const colors = props.type === 'income' 
    ? ['#67c23a', '#95d475', '#b3e19d', '#e1f3d8']
    : ['#f56c6c', '#f89898', '#fab6b6', '#fcd3d3']

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: function(params) {
        return `${params.name}: ¥${params.value} (${params.percent}%)`
      }
    },
    legend: {
      type: 'scroll',
      orient: 'vertical',
      right: 10,
      top: 20,
      bottom: 20,
    },
    series: [
      {
        name: props.type === 'income' ? '收入分类' : '支出分类',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        color: colors,
        data: Object.entries(data).map(([name, value]) => ({
          name,
          value: value.toFixed(2)
        }))
      }
    ]
  }
  chartInstance.setOption(option)
}

// 工具函数
const formatNumber = (num) => {
  return num.toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// 监听窗口大小变化
window.addEventListener('resize', () => {
  yearlyChartInstance?.resize()
  chartInstance?.resize()
})

// 生命周期钩子
onMounted(() => {
  selectedYear.value = currentYear
  loadMonthlyData()
  loadYearlyData()
  loadCategoryData()
})

watch(selectedYear, (newValue) => {
  if (newValue && !isNaN(newValue)) {
    loadYearlyData()
  }
})

// 组件卸载时清理
onUnmounted(() => {
  window.removeEventListener('resize', () => {
    yearlyChartInstance?.resize()
  })
  yearlyChartInstance?.dispose()
})
</script>

<style scoped>
.statistics {
  padding: 20px;
}

.mb-4 {
  margin-bottom: 20px;
}

.mt-4 {
  margin-top: 20px;
}

.stat-card {
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.stat-title {
  font-size: 16px;
  color: #606266;
}

.stat-trend {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #909399;
  font-size: 14px;
}

.increase {
  color: #67c23a;
}

.decrease {
  color: #f56c6c;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.yearly-chart {
  height: 400px;
  width: 100%;
}
</style> 