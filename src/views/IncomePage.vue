<template>
  <div class="income-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="form-card">
          <template #header>
            <div class="card-header">
              <span>添加收入</span>
            </div>
          </template>
          <IncomeForm :onAddIncome="handleAddIncome" />
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>收入记录</span>
              <div class="header-actions">
                <el-input
                  v-model="searchQuery"
                  placeholder="搜索收入记录"
                  prefix-icon="Search"
                  clearable
                />
                <el-select v-model="categoryFilter" placeholder="类别筛选" clearable>
                  <el-option
                    v-for="category in categories"
                    :key="category"
                    :label="category"
                    :value="category"
                  />
                </el-select>
              </div>
            </div>
          </template>
          <IncomeList 
            :incomes="filteredIncomes"
            @delete-income="handleDeleteIncome" 
          />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useStorage } from '@vueuse/core'
import IncomeForm from '../components/IncomeForm.vue'
import IncomeList from '../components/IncomeList.vue'

const incomes = useStorage('incomes', [])
const searchQuery = ref('')
const categoryFilter = ref('')

const categories = [
  '工资', '奖金', '投资', '兼职', '其他'
]

const filteredIncomes = computed(() => {
  return incomes.value
    .filter(income => 
      income.description.toLowerCase().includes(searchQuery.value.toLowerCase()) &&
      (!categoryFilter.value || income.category === categoryFilter.value)
    )
    .sort((a, b) => new Date(b.date) - new Date(a.date))
})

const handleAddIncome = (incomeData) => {
  const newIncome = {
    _id: Date.now().toString(),
    ...incomeData,
    date: new Date(),
    amount: Number(incomeData.amount)
  }
  incomes.value = [newIncome, ...incomes.value]
}

const handleDeleteIncome = (id) => {
  incomes.value = incomes.value.filter(income => income._id !== id)
}
</script>

<style scoped>
.income-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.form-card {
  position: sticky;
  top: 20px;
}
</style> 