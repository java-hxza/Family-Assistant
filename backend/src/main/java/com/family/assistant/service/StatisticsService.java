package com.family.assistant.service;

import com.family.assistant.entity.Expense;
import com.family.assistant.entity.Income;
import com.family.assistant.vo.StatisticsVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public interface StatisticsService {
    /**
     * 获取月度统计数据
     */
    StatisticsVO getMonthlyStatistics(Long userId);

    /**
     * 获取年度统计数据
     * @param year 年份
     */
    Map<String, Object> getYearlyStatistics(Integer year, Long userId);

    /**
     * 获取支出类别统计
     */
    Map<String, Double> getExpenseCategories(Long userId);

    /**
     * 获取收入类别统计
     */
    Map<String, Double> getIncomeCategories(Long userId);

    /**
     * 获取最近交易记录（分页）
     * @param userId 用户ID
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页后的交易记录
     */
    IPage<Map<String, Object>> getRecentTransactions(Long userId, Integer current, Integer size);
} 