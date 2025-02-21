package com.family.assistant.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.assistant.entity.Income;
import com.family.assistant.mapper.IncomeMapper;
import com.family.assistant.service.IncomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class IncomeServiceImpl extends ServiceImpl<IncomeMapper, Income> implements IncomeService {

    private static final Logger log = LoggerFactory.getLogger(IncomeServiceImpl.class);

    @Override
    public Income add(Income income) {
        log.info("Adding new income: {}", income);
        save(income);
        log.info("Successfully added income with id: {}", income.getId());
        return income;
    }

    @Override
    public void delete(Long id, Long userId) {
        lambdaUpdate()
            .eq(Income::getId, id)
            .eq(Income::getUserId, userId)
            .remove();
    }

    @Override
    public List<Income> getByUserId(Long userId) {
        return lambdaQuery()
            .eq(Income::getUserId, userId)
            .orderByDesc(Income::getIncomeDate)
            .list();
    }

    @Override
    public List<Income> getByUserIdAndDateRange(Long userId, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);

        return lambdaQuery()
            .eq(Income::getUserId, userId)
            .between(Income::getIncomeDate, start, end)
            .orderByDesc(Income::getIncomeDate)
            .list();
    }
} 