<template>
  <div class="expense-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="form-card">
          <template #header>
            <div class="card-header">
              <span>添加支出</span>
            </div>
          </template>
          <ExpenseForm :onAddExpense="handleAddExpense" />
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>支出记录</span>
              <div class="header-actions">
                <el-input
                  v-model="searchQuery"
                  placeholder="搜索支出记录"
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
          <ExpenseList 
            :expenses="filteredExpenses"
            @delete-expense="handleDeleteExpense" 
          />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useStorage } from '@vueuse/core'
import ExpenseForm from '../components/ExpenseForm.vue'
import ExpenseList from '../components/ExpenseList.vue'

const expenses = useStorage('expenses', [])
const searchQuery = ref('')
const categoryFilter = ref('')

const categories = [
  '食品', '交通', '住房', '娱乐', '购物', '医疗', '教育', '其他'
]

const filteredExpenses = computed(() => {
  return expenses.value
    .filter(expense => 
      expense.description.toLowerCase().includes(searchQuery.value.toLowerCase()) &&
      (!categoryFilter.value || expense.category === categoryFilter.value)
    )
    .sort((a, b) => new Date(b.date) - new Date(a.date))
})

const handleAddExpense = (expenseData) => {
  const newExpense = {
    _id: Date.now().toString(),
    ...expenseData,
    date: new Date(),
    amount: Number(expenseData.amount)
  }
  expenses.value = [newExpense, ...expenses.value]
}

const handleDeleteExpense = (id) => {
  expenses.value = expenses.value.filter(expense => expense._id !== id)
}
</script>

<style scoped>
.expense-page {
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