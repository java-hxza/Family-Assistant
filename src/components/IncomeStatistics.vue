<template>
  <div class="income-statistics">
    <div ref="pieChart" style="height: 300px" v-loading="loading">
      <el-empty v-if="!hasData" description="暂无数据" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'
import { getIncomeCategories } from '../api/statistics'
import { ElMessage } from 'element-plus'

const props = defineProps({
  incomes: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const loading = ref(false)
const hasData = ref(false)
const pieChart = ref(null)
let chartInstance = null

const loadData = async () => {
  loading.value = true
  try {
    const res = await getIncomeCategories()
    const data = res.data
    hasData.value = Object.keys(data).length > 0
    if (hasData.value) {
      initChart(data)
    }
  } catch (error) {
    console.error('Failed to load income categories:', error)
    ElMessage.error('加载收入分类统计失败')
  } finally {
    loading.value = false
  }
}

const initChart = (data) => {
  if (!chartInstance) {
    chartInstance = echarts.init(pieChart.value)
  }

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
        name: '收入分类',
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
        data: Object.entries(data).map(([name, value]) => ({
          name,
          value: value.toFixed(2)
        }))
      }
    ]
  }
  chartInstance.setOption(option)
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
})

onUnmounted(() => {
  window.removeEventListener('resize', () => {
    chartInstance?.resize()
  })
  chartInstance?.dispose()
})

watch(props.incomes, () => {
  loadData()
})
</script>

<style scoped>
.income-statistics {
  height: 100%;
  min-height: 300px;
}
</style> 