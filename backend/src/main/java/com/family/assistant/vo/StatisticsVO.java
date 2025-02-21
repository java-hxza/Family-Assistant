package com.family.assistant.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class StatisticsVO {
    // 月度统计
    private BigDecimal monthlyExpense;    // 月支出
    private BigDecimal monthlyIncome;     // 月收入
    private BigDecimal monthlyBalance;    // 月结余
    
    // 环比变化
    private Double expenseChange;         // 支出变化率
    private Double incomeChange;          // 收入变化率
    private Double balanceChange;         // 结余变化率
    
    // 分类统计
    private Map<String, Object> expenseByCategory;  // 支出分类统计
    private Map<String, Object> incomeByCategory;   // 收入分类统计
} 