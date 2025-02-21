package com.family.assistant.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.family.assistant.service.StatisticsService;
import com.family.assistant.vo.StatisticsVO;
import com.family.assistant.mapper.ExpenseMapper;
import com.family.assistant.mapper.IncomeMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.family.assistant.entity.Expense;
import com.family.assistant.entity.Income;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final Logger log = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    @Autowired
    private ExpenseMapper expenseMapper;

    @Autowired
    private IncomeMapper incomeMapper;

    private BigDecimal toBigDecimal(Object value) {
        return Convert.toBigDecimal(value, BigDecimal.ZERO).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public StatisticsVO getMonthlyStatistics(Long userId) {
        StatisticsVO vo = new StatisticsVO();
        Date now = new Date();
        Date startOfMonth = DateUtil.beginOfMonth(now);
        Date endOfMonth = DateUtil.endOfMonth(now);

        try {
            // 获取本月数据
            BigDecimal currentMonthExpense = toBigDecimal(expenseMapper.sumByDateRange(
                    DateUtil.format(startOfMonth, "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.format(endOfMonth, "yyyy-MM-dd HH:mm:ss"),
                    userId));
            BigDecimal currentMonthIncome = toBigDecimal(incomeMapper.sumByDateRange(
                    DateUtil.format(startOfMonth, "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.format(endOfMonth, "yyyy-MM-dd HH:mm:ss"),
                    userId));

            vo.setMonthlyExpense(currentMonthExpense);
            vo.setMonthlyIncome(currentMonthIncome);
            vo.setMonthlyBalance(currentMonthIncome.subtract(currentMonthExpense));

            // 上月数据
            Date lastMonth = DateUtil.offsetMonth(now, -1);
            Date lastMonthStart = DateUtil.beginOfMonth(lastMonth);
            Date lastMonthEnd = DateUtil.endOfMonth(lastMonth);

            BigDecimal lastMonthExpense = toBigDecimal(expenseMapper.sumByDateRange(
                    DateUtil.format(lastMonthStart, "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.format(lastMonthEnd, "yyyy-MM-dd HH:mm:ss"),
                    userId));
            BigDecimal lastMonthIncome = toBigDecimal(incomeMapper.sumByDateRange(
                    DateUtil.format(lastMonthStart, "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.format(lastMonthEnd, "yyyy-MM-dd HH:mm:ss"),
                    userId));

            // 计算环比变化
            vo.setExpenseChange(calculateChange(lastMonthExpense, currentMonthExpense));
            vo.setIncomeChange(calculateChange(lastMonthIncome, currentMonthIncome));
            vo.setBalanceChange(calculateChange(
                    lastMonthIncome.subtract(lastMonthExpense),
                    vo.getMonthlyBalance()));

            // 获取分类统计
            List<Map<String, Object>> expenseCategories = expenseMapper.sumByCategory(
                    DateUtil.format(startOfMonth, "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.format(endOfMonth, "yyyy-MM-dd HH:mm:ss"),
                    userId);
            List<Map<String, Object>> incomeCategories = incomeMapper.sumByCategory(
                    DateUtil.format(startOfMonth, "yyyy-MM-dd HH:mm:ss"),
                    DateUtil.format(endOfMonth, "yyyy-MM-dd HH:mm:ss"),
                    userId);

            // 转换为Map<String, Object>
            Map<String, Object> expenseCategoryMap = new HashMap<>();
            Map<String, Object> incomeCategoryMap = new HashMap<>();

            for (Map<String, Object> category : expenseCategories) {
                expenseCategoryMap.put(
                    Convert.toStr(category.get("category"), "其他"),
                    Convert.toBigDecimal(category.get("total"), BigDecimal.ZERO)
                );
            }

            for (Map<String, Object> category : incomeCategories) {
                incomeCategoryMap.put(
                    Convert.toStr(category.get("category"), "其他"),
                    Convert.toBigDecimal(category.get("total"), BigDecimal.ZERO)
                );
            }

            vo.setExpenseByCategory(expenseCategoryMap);
            vo.setIncomeByCategory(incomeCategoryMap);

        } catch (Exception e) {
            log.error("获取月度统计数据失败", e);
            vo.setExpenseByCategory(new HashMap<>());
            vo.setIncomeByCategory(new HashMap<>());
        }

        return vo;
    }

    @Override
    public Map<String, Object> getYearlyStatistics(Integer year, Long userId) {
        Map<String, Object> result = new HashMap<>();

        try {
            if (year == null) {
                year = LocalDateTime.now().getYear();
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            Map<Integer, BigDecimal> monthlyExpenses = new HashMap<>();
            Map<Integer, BigDecimal> monthlyIncomes = new HashMap<>();

            // 按月统计
            for (int month = 1; month <= 12; month++) {
                LocalDateTime monthStart = LocalDateTime.of(year, month, 1, 0, 0);
                LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);

                String startDate = monthStart.format(formatter);
                String endDate = monthEnd.format(formatter);

                // 获取月度支出
                BigDecimal expenses = toBigDecimal(expenseMapper.sumByDateRange(
                    startDate, endDate, userId));
                monthlyExpenses.put(month, expenses);

                // 获取月度收入
                BigDecimal incomes = toBigDecimal(incomeMapper.sumByDateRange(
                    startDate, endDate, userId));
                monthlyIncomes.put(month, incomes);
            }

            // 获取年度总计
            LocalDateTime yearStart = LocalDateTime.of(year, 1, 1, 0, 0);
            LocalDateTime yearEnd = yearStart.plusYears(1).minusSeconds(1);

            BigDecimal yearlyExpense = toBigDecimal(expenseMapper.sumByDateRange(
                yearStart.format(formatter),
                yearEnd.format(formatter),
                userId));

            BigDecimal yearlyIncome = toBigDecimal(incomeMapper.sumByDateRange(
                yearStart.format(formatter),
                yearEnd.format(formatter),
                userId));

            result.put("monthlyExpenses", monthlyExpenses);
            result.put("monthlyIncomes", monthlyIncomes);
            result.put("yearlyExpense", yearlyExpense);
            result.put("yearlyIncome", yearlyIncome);
            result.put("yearlyBalance", yearlyIncome.subtract(yearlyExpense));

        } catch (Exception e) {
            log.error("获取年度统计数据失败", e);
            result.put("monthlyExpenses", new HashMap<>());
            result.put("monthlyIncomes", new HashMap<>());
            result.put("yearlyExpense", BigDecimal.ZERO);
            result.put("yearlyIncome", BigDecimal.ZERO);
            result.put("yearlyBalance", BigDecimal.ZERO);
        }

        return result;
    }

    @Override
    public Map<String, Double> getExpenseCategories(Long userId) {
        Date now = new Date();
        Date startOfMonth = DateUtil.beginOfMonth(now);
        Date endOfMonth = DateUtil.endOfMonth(now);

        List<Map<String, Object>> categoryList = expenseMapper.sumByCategory(
                DateUtil.format(startOfMonth, "yyyy-MM-dd HH:mm:ss"),
                DateUtil.format(endOfMonth, "yyyy-MM-dd HH:mm:ss"),
                userId);

        return calculateAmounts(categoryList);
    }

    @Override
    public Map<String, Double> getIncomeCategories(Long userId) {
        Date now = new Date();
        Date startOfMonth = DateUtil.beginOfMonth(now);
        Date endOfMonth = DateUtil.endOfMonth(now);

        List<Map<String, Object>> categoryList = incomeMapper.sumByCategory(
                DateUtil.format(startOfMonth, "yyyy-MM-dd HH:mm:ss"),
                DateUtil.format(endOfMonth, "yyyy-MM-dd HH:mm:ss"),
                userId);

        return calculateAmounts(categoryList);
    }

    private Map<String, Double> calculateAmounts(List<Map<String, Object>> categoryList) {
        Map<String, Double> result = MapUtil.newHashMap();
        if (categoryList == null || categoryList.isEmpty()) {
            return result;
        }

        try {
            // 计算每个类别的金额
            categoryList.forEach(map -> {
                String category = Convert.toStr(map.get("category"), "其他");
                BigDecimal amount = toBigDecimal(map.get("total"));
                result.put(category, amount.doubleValue());
            });
        } catch (Exception e) {
            log.error("计算金额失败", e);
        }

        return result;
    }

    private Double calculateChange(BigDecimal last, BigDecimal current) {
        try {
            last = toBigDecimal(last);
            current = toBigDecimal(current);

            if (last.compareTo(BigDecimal.ZERO) == 0) {
                return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
            }

            return current.subtract(last)
                    .multiply(new BigDecimal("100"))
                    .divide(last, 2, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
        } catch (Exception e) {
            log.error("计算变化率失败", e);
            return 0.0;
        }
    }

    @Override
    public IPage<Map<String, Object>> getRecentTransactions(Long userId, Integer current, Integer size) {
        Page<Map<String, Object>> page = new Page<>(current, size);
        List<Map<String, Object>> result = new ArrayList<>();
        
        try {
            // 获取支出记录
            QueryWrapper<Expense> expenseQuery = new QueryWrapper<>();
            expenseQuery.eq("user_id", userId)
                       .eq("deleted", 0)
                       .orderByDesc("expense_date");
            List<Expense> expenses = expenseMapper.selectList(expenseQuery);
            
            // 获取收入记录
            QueryWrapper<Income> incomeQuery = new QueryWrapper<>();
            incomeQuery.eq("user_id", userId)
                       .eq("deleted", 0)
                       .orderByDesc("income_date");
            List<Income> incomes = incomeMapper.selectList(incomeQuery);
            
            // 合并并转换记录
            for (Expense expense : expenses) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("id", expense.getId());
                transaction.put("type", "expense");
                transaction.put("amount", expense.getAmount());
                transaction.put("category", expense.getCategory());
                transaction.put("description", expense.getDescription());
                transaction.put("date", DateUtil.format(expense.getExpenseDate(), "yyyy-MM-dd HH:mm:ss"));
                result.add(transaction);
            }
            
            for (Income income : incomes) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("id", income.getId());
                transaction.put("type", "income");
                transaction.put("amount", income.getAmount());
                transaction.put("category", income.getCategory());
                transaction.put("description", income.getDescription());
                transaction.put("date", DateUtil.format(income.getIncomeDate(), "yyyy-MM-dd HH:mm:ss"));
                result.add(transaction);
            }
            
            // 按日期排序
            result.sort((a, b) -> b.get("date").toString().compareTo(a.get("date").toString()));
            
            // 手动分页
            int total = result.size();
            int start = (current - 1) * size;
            int end = Math.min(start + size, total);
            
            List<Map<String, Object>> pageRecords = result.subList(start, end);
            
            // 设置分页信息
            page.setRecords(pageRecords);
            page.setTotal(total);
            
            return page;
        } catch (Exception e) {
            log.error("获取最近交易记录失败", e);
            page.setRecords(new ArrayList<>());
            page.setTotal(0);
            return page;
        }
    }
} 