<template>
  <div class="expense-form">
    <el-form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label>金额：</label>
        <el-input
          v-model="formData.amount"
          type="number"
          placeholder="请输入支出金额"
          prefix-icon="Money"
        >
          <template #prefix>¥</template>
        </el-input>
      </div>
      <div class="form-group">
        <label>类别：</label>
        <el-select v-model="formData.category" placeholder="选择类别" class="category-select">
          <el-option
            v-for="(category, index) in categories"
            :key="index"
            :label="category.label"
            :value="category.value"
          >
            <div class="category-option">
              <el-icon :class="category.iconClass">
                <component :is="category.icon" />
              </el-icon>
              <span>{{ category.label }}</span>
            </div>
          </el-option>
        </el-select>
      </div>
      <div class="form-group">
        <label>描述：</label>
        <el-input
          v-model="formData.description"
          type="text"
          placeholder="请输入支出描述"
          prefix-icon="Memo"
        />
      </div>
      <el-button type="primary" native-type="submit" class="submit-btn" :icon="Plus">
        添加支出
      </el-button>
    </el-form>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { addExpense } from '../api/expense'
import { 
  Money, 
  Food, 
  Van, 
  House, 
  Film, 
  ShoppingCart, 
  FirstAidKit, 
  Reading, 
  More,
  Plus,
  Memo 
} from '@element-plus/icons-vue'

const emit = defineEmits(['expense-added'])

const categories = [
  { label: '食品', value: '食品', icon: 'Food', iconClass: 'food-icon' },
  { label: '交通', value: '交通', icon: 'Van', iconClass: 'transport-icon' },
  { label: '住房', value: '住房', icon: 'House', iconClass: 'house-icon' },
  { label: '娱乐', value: '娱乐', icon: 'Film', iconClass: 'entertainment-icon' },
  { label: '购物', value: '购物', icon: 'ShoppingCart', iconClass: 'shopping-icon' },
  { label: '医疗', value: '医疗', icon: 'FirstAidKit', iconClass: 'medical-icon' },
  { label: '教育', value: '教育', icon: 'Reading', iconClass: 'education-icon' },
  { label: '其他', value: '其他', icon: 'More', iconClass: 'other-icon' }
]

const formData = reactive({
  amount: '',
  category: '',
  description: ''
})

const handleSubmit = async () => {
  if (!formData.amount || !formData.category || !formData.description) {
    ElMessage.warning('请填写完整信息')
    return
  }

  try {
    const res = await addExpense({
      ...formData,
      expenseDate: new Date().toISOString()
    })
    ElMessage.success('添加成功')
    emit('expense-added', res.data)
    formData.amount = ''
    formData.category = ''
    formData.description = ''
  } catch (error) {
    ElMessage.error('添加失败')
  }
}
</script>

<style scoped>
.expense-form {
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #2c3e50;
  font-weight: 500;
}

.category-select {
  width: 100%;
}

.category-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.submit-btn {
  width: 100%;
  margin-top: 20px;
  height: 40px;
  font-size: 16px;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  box-shadow: 0 0 0 1px #dcdfe6;
}

:deep(.el-input__wrapper:hover),
:deep(.el-select__wrapper:hover) {
  box-shadow: 0 0 0 1px #409EFF;
}

/* 类别图标颜色 */
.food-icon { color: #FF9800; }
.transport-icon { color: #2196F3; }
.house-icon { color: #4CAF50; }
.entertainment-icon { color: #9C27B0; }
.shopping-icon { color: #E91E63; }
.medical-icon { color: #F44336; }
.education-icon { color: #3F51B5; }
.other-icon { color: #607D8B; }

/* 添加动画效果 */
.submit-btn {
  transition: transform 0.2s ease;
}

.submit-btn:hover {
  transform: translateY(-2px);
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  transition: box-shadow 0.3s ease;
}
</style> 