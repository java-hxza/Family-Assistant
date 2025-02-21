package com.family.assistant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.assistant.entity.Expense;
import com.family.assistant.mapper.ExpenseMapper;
import com.family.assistant.service.ExpenseService;
import com.family.assistant.util.LogUtil;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExpenseServiceImpl extends ServiceImpl<ExpenseMapper, Expense> implements ExpenseService {

    private static final Logger log = LogUtil.getLogger();

    @Override
    public Expense add(Expense expense) {
        log.info("Adding new expense: {}", expense);
        try {
            save(expense);
            log.info("Successfully added expense with id: {}", expense.getId());
            return expense;
        } catch (Exception e) {
            log.error("Failed to add expense: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to add expense", e);
        }
    }

    @Override
    public void delete(Long id, Long userId) {
        log.info("Deleting expense with id: {} for user: {}", id, userId);
        try {
            lambdaUpdate()
                .eq(Expense::getId, id)
                .eq(Expense::getUserId, userId)
                .remove();
            log.info("Successfully deleted expense");
        } catch (Exception e) {
            log.error("Failed to delete expense: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete expense", e);
        }
    }

    @Override
    public List<Expense> getByUserId(Long userId) {
        log.info("Getting expenses for user: {}", userId);
        try {
            List<Expense> expenses = lambdaQuery()
                .eq(Expense::getUserId, userId)
                .orderByDesc(Expense::getExpenseDate)
                .list();
            log.info("Found {} expenses", expenses.size());
            return expenses;
        } catch (Exception e) {
            log.error("Failed to get expenses: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get expenses", e);
        }
    }

    @Override
    public List<Expense> getByUserIdAndDateRange(Long userId, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);

        return lambdaQuery()
            .eq(Expense::getUserId, userId)
            .between(Expense::getExpenseDate, start, end)
            .orderByDesc(Expense::getExpenseDate)
            .list();
    }
} 