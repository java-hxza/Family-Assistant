package com.family.assistant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.family.assistant.common.Result;
import com.family.assistant.service.StatisticsService;
import com.family.assistant.vo.StatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/monthly")
    public Result<StatisticsVO> getMonthlyStatistics() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Result.success(statisticsService.getMonthlyStatistics(getUserIdFromUsername(username)));
    }

    @GetMapping("/yearly")
    public Result<Map<String, Object>> getYearlyStatistics(@RequestParam(required = false) Integer year) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Result.success(statisticsService.getYearlyStatistics(year, getUserIdFromUsername(username)));
    }

    @GetMapping("/expense-categories")
    public Result<Map<String, Double>> getExpenseCategories() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Result.success(statisticsService.getExpenseCategories(getUserIdFromUsername(username)));
    }

    @GetMapping("/income-categories")
    public Result<Map<String, Double>> getIncomeCategories() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Result.success(statisticsService.getIncomeCategories(getUserIdFromUsername(username)));
    }

    @GetMapping("/recent-transactions")
    public Result<IPage<Map<String, Object>>> getRecentTransactions(
        @RequestParam(defaultValue = "1") Integer current,
        @RequestParam(defaultValue = "10") Integer size
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Result.success(statisticsService.getRecentTransactions(
            getUserIdFromUsername(username), current, size));
    }

    private Long getUserIdFromUsername(String username) {
        // 临时返回固定值，实际项目中需要查询数据库
        return 1L;
    }
} 