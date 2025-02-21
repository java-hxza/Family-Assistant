<template>
  <div class="income-list">
    <el-table 
      :data="incomes" 
      style="width: 100%"
      :default-sort="{ prop: 'date', order: 'descending' }"
      v-loading="loading"
    >
      <el-table-column prop="incomeDate" label="日期" width="180" sortable>
        <template #default="{ row }">
          <el-tooltip 
            :content="formatFullDate(row.incomeDate)" 
            placement="top"
            effect="light"
          >
            <span>{{ formatTableDate(row) }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="金额" width="150" sortable>
        <template #default="{ row }">
          <span class="amount">¥{{ Number(row.amount).toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="category" label="类别" width="120">
        <template #default="{ row }">
          <el-tag :type="getCategoryTagType(row.category)" effect="light">
            {{ row.category }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述">
        <template #default="{ row }">
          <el-tooltip 
            :content="row.description" 
            placement="top" 
            :disabled="row.description.length < 20"
          >
            <span class="description">{{ row.description }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button
            type="danger"
            size="small"
            @click="handleDelete(row)"
            :icon="Delete"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页器 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 删除确认对话框 -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="确认删除"
      width="30%"
      center
    >
      <span>确定要删除这条收入记录吗？</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import { getIncomeList, addIncome, updateIncome, deleteIncome } from '../api/income'
import { formatDate } from '../utils/dateFormat'

const incomes = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadIncomes = async () => {
  loading.value = true
  try {
    const res = await getIncomeList({
      current: currentPage.value,
      size: pageSize.value
    })
    incomes.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('Failed to load incomes:', error)
    ElMessage.error('加载收入记录失败')
  } finally {
    loading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条收入记录吗？', '提示', {
      type: 'warning'
    })
    await deleteIncome(row.id)
    ElMessage.success('删除成功')
    await loadIncomes()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadIncomes()
})

const deleteDialogVisible = ref(false)
const currentDeleteId = ref(null)

const formatTableDate = (row) => {
  return formatDate(row.incomeDate)
}

const formatFullDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const getCategoryTagType = (category) => {
  const typeMap = {
    '工资': 'success',
    '奖金': 'warning',
    '投资': 'primary',
    '兼职': '',
    '其他': 'info'
  }
  return typeMap[category] || ''
}

const confirmDelete = () => {
  deleteDialogVisible.value = false
  currentDeleteId.value = null
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  loadIncomes()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadIncomes()
}
</script>

<style scoped>
.income-list {
  margin-top: 20px;
}

.amount {
  color: #67c23a;
  font-weight: 600;
}

.description {
  display: inline-block;
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

:deep(.el-table__header) {
  background-color: #f5f7fa;
}

:deep(.el-table__row) {
  transition: background-color 0.3s ease;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa !important;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 添加表格动画 */
:deep(.el-table__body-wrapper) {
  transition: all 0.3s ease;
}

/* 优化标签样式 */
:deep(.el-tag) {
  border-radius: 4px;
  padding: 0 8px;
}

/* 优化按钮样式 */
:deep(.el-button) {
  transition: all 0.3s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 