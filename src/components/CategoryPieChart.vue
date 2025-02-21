<template>
  <div class="category-statistics">
    <div ref="pieChart" style="height: 300px" v-loading="loading">
      <el-empty v-if="!hasData" description="暂无数据" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

const props = defineProps({
  data: {
    type: Object,
    default: () => ({})
  },
  loading: {
    type: Boolean,
    default: false
  },
  type: {
    type: String,
    required: true,
    validator: (value) => ['income', 'expense'].includes(value)
  }
})

const pieChart = ref(null)
let chartInstance = null
const hasData = ref(false)

const initChart = (data) => {
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

watch(() => props.data, (newData) => {
  hasData.value = Object.keys(newData).length > 0
  if (hasData.value) {
    initChart(newData)
  }
}, { deep: true })

onMounted(() => {
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
</script>

<style scoped>
.category-statistics {
  height: 100%;
  min-height: 300px;
}
</style> 