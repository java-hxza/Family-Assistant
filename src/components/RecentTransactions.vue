<template>
  <div class="recent-transactions">
    <el-table :data="transactions" style="width: 100%" v-loading="loading">
      <el-table-column prop="date" label="日期" width="180">
        <template #default="{ row }">
          {{ formatDate(row.date) }}
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="金额" width="150">
        <template #default="{ row }">
          <span :class="{ 'income': isIncome(row), 'expense': !isIncome(row) }">
            {{ isIncome(row) ? '+' : '-' }}¥{{ Number(row.amount).toFixed(2) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="category" label="类别" width="120" />
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="isIncome(row) ? 'success' : 'danger'">
            {{ isIncome(row) ? '收入' : '支出' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
    <div class="pagination-container">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        @update:current-page="(val) => $emit('update:currentPage', val)"
        @update:page-size="(val) => $emit('update:pageSize', val)"
      />
    </div>
  </div>
</template>

<script setup>
defineProps({
  transactions: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  total: {
    type: Number,
    default: 0
  },
  currentPage: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 10
  }
})

const emit = defineEmits([
  'size-change',
  'current-change',
  'update:currentPage',
  'update:pageSize'
])

const formatDate = (date) => {
  return new Date(date).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const isIncome = (transaction) => {
  // 通过检查交易记录的来源判断是收入还是支出
  return transaction.hasOwnProperty('source')
}

const handleSizeChange = (val) => {
  emit('size-change', val)
  emit('update:pageSize', val)
}

const handleCurrentChange = (val) => {
  emit('current-change', val)
  emit('update:currentPage', val)
}
</script>

<style scoped>
.recent-transactions {
  margin-top: 10px;
}

.income {
  color: #67c23a;
  font-weight: 600;
}

.expense {
  color: #f56c6c;
  font-weight: 600;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 