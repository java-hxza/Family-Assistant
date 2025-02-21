package com.family.assistant.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.family.assistant.entity.Income;
import java.util.List;

public interface IncomeService extends IService<Income> {
    Income add(Income income);
    void delete(Long id, Long userId);
    List<Income> getByUserId(Long userId);
    List<Income> getByUserIdAndDateRange(Long userId, String startDate, String endDate);
} 