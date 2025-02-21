package com.family.assistant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.family.assistant.entity.Expense;
import java.util.List;

public interface ExpenseService extends IService<Expense> {
    Expense add(Expense expense);
    void delete(Long id, Long userId);
    List<Expense> getByUserId(Long userId);
    List<Expense> getByUserIdAndDateRange(Long userId, String startDate, String endDate);
} 