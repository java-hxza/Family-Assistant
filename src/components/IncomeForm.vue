<template>
  <div class="income-form">
    <el-form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label>金额：</label>
        <el-input
          v-model="formData.amount"
          type="number"
          placeholder="请输入收入金额"
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
          placeholder="请输入收入描述"
          prefix-icon="Memo"
        />
      </div>
      <el-button type="success" native-type="submit" class="submit-btn" :icon="Plus">
        添加收入
      </el-button>
    </el-form>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { addIncome } from '../api/income'
import { 
  Money, 
  Wallet,
  Medal,
  TrendCharts,
  Service,
  More,
  Plus,
  Memo 
} from '@element-plus/icons-vue'

const emit = defineEmits(['income-added'])

const categories = [
  { label: '工资', value: '工资', icon: 'Wallet', iconClass: 'salary-icon' },
  { label: '奖金', value: '奖金', icon: 'Medal', iconClass: 'bonus-icon' },
  { label: '投资', value: '投资', icon: 'TrendCharts', iconClass: 'investment-icon' },
  { label: '兼职', value: '兼职', icon: 'Service', iconClass: 'part-time-icon' },
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
    const res = await addIncome({
      ...formData,
      incomeDate: new Date().toISOString()
    })
    ElMessage.success('添加成功')
    emit('income-added', res.data)
    formData.amount = ''
    formData.category = ''
    formData.description = ''
  } catch (error) {
    ElMessage.error('添加失败')
  }
}
</script>

<style scoped>
.income-form {
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
  box-shadow: 0 0 0 1px #67c23a;
}

/* 类别图标颜色 */
.salary-icon { color: #67c23a; }
.bonus-icon { color: #e6a23c; }
.investment-icon { color: #409eff; }
.part-time-icon { color: #9c27b0; }
.other-icon { color: #909399; }

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