package com.family.assistant.util;

import cn.hutool.core.util.RandomUtil;
import com.family.assistant.entity.Expense;
import com.family.assistant.entity.Income;
import com.family.assistant.mapper.ExpenseMapper;
import com.family.assistant.mapper.IncomeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestDataGenerator {

    @Autowired
    private ExpenseMapper expenseMapper;

    @Autowired
    private IncomeMapper incomeMapper;

    private static final String[] EXPENSE_CATEGORIES = {
            "餐饮", "交通", "购物", "娱乐", "居住", "医疗", "教育", "其他"
    };

    private static final String[] INCOME_CATEGORIES = {
            "工资", "奖金", "投资", "兼职", "其他"
    };

    @Transactional
    public void generateTestData(int year, Long userId) {
        // 生成每月的数据
        for (int month = 1; month <= 12; month++) {
            generateMonthlyData(year, month, userId);
        }
    }

    private LocalDateTime generateRandomDateTime(int year, int month, int daysInMonth) {
        int day = RandomUtil.randomInt(1, daysInMonth + 1);
        int hour = RandomUtil.randomInt(8, 23);
        int minute = RandomUtil.randomInt(0, 60);
        int second = RandomUtil.randomInt(0, 60);
        return LocalDateTime.of(year, month, day, hour, minute, second);
    }

    private void generateMonthlyData(int year, int month, Long userId) {
        // 获取当月天数
        int daysInMonth = LocalDateTime.of(year, month, 1, 0, 0)
                .toLocalDate().lengthOfMonth();

        // 生成支出数据
        List<Expense> expenses = new ArrayList<>();
        int expenseCount = RandomUtil.randomInt(20, 40); // 每月20-40笔支出
        for (int i = 0; i < expenseCount; i++) {
            Expense expense = new Expense();
            expense.setUserId(userId);
            expense.setCategory(EXPENSE_CATEGORIES[RandomUtil.randomInt(EXPENSE_CATEGORIES.length)]);
            
            // 根据类别设置不同范围的金额
            double maxAmount;
            switch (expense.getCategory()) {
                case "餐饮":
                    maxAmount = 200.0;
                    break;
                case "交通":
                    maxAmount = 100.0;
                    break;
                case "购物":
                    maxAmount = 1000.0;
                    break;
                case "娱乐":
                    maxAmount = 500.0;
                    break;
                case "居住":
                    maxAmount = 3000.0;
                    break;
                case "医疗":
                    maxAmount = 2000.0;
                    break;
                case "教育":
                    maxAmount = 5000.0;
                    break;
                default:
                    maxAmount = 1000.0;
                    break;
            }
            expense.setAmount(new BigDecimal(RandomUtil.randomDouble(10.0, maxAmount)).setScale(2, BigDecimal.ROUND_HALF_UP));
            expense.setDescription("测试支出-" + expense.getCategory());
            
            // 生成随机日期时间
            LocalDateTime expenseDate = generateRandomDateTime(year, month, daysInMonth);
            expense.setExpenseDate(expenseDate);
            expense.setCreateTime(expenseDate);
            expense.setUpdateTime(expenseDate);
            expenses.add(expense);
        }

        // 生成收入数据
        List<Income> incomes = new ArrayList<>();
        int incomeCount = RandomUtil.randomInt(3, 8); // 每月3-8笔收入
        for (int i = 0; i < incomeCount; i++) {
            Income income = new Income();
            income.setUserId(userId);
            income.setCategory(INCOME_CATEGORIES[RandomUtil.randomInt(INCOME_CATEGORIES.length)]);
            
            // 根据类别设置不同范围的金额
            double maxAmount;
            switch (income.getCategory()) {
                case "工资":
                    maxAmount = 20000.0;
                    break;
                case "奖金":
                    maxAmount = 50000.0;
                    break;
                case "投资":
                    maxAmount = 10000.0;
                    break;
                case "兼职":
                    maxAmount = 5000.0;
                    break;
                default:
                    maxAmount = 3000.0;
                    break;
            }
            income.setAmount(new BigDecimal(RandomUtil.randomDouble(1000.0, maxAmount)).setScale(2, BigDecimal.ROUND_HALF_UP));
            income.setDescription("测试收入-" + income.getCategory());
            
            // 生成随机日期时间
            LocalDateTime incomeDate = generateRandomDateTime(year, month, daysInMonth);
            income.setIncomeDate(incomeDate);
            income.setCreateTime(incomeDate);
            income.setUpdateTime(incomeDate);
            incomes.add(income);
        }

        // 批量插入数据
        for (Expense expense : expenses) {
            expenseMapper.insert(expense);
        }
        for (Income income : incomes) {
            incomeMapper.insert(income);
        }
    }
} 