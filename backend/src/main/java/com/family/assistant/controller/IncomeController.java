package com.family.assistant.controller;

import com.family.assistant.common.Result;
import com.family.assistant.entity.Income;
import com.family.assistant.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

@RestController
@RequestMapping("/api/income")
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    @GetMapping("/list")
    public Result<IPage<Income>> list(
        @RequestParam(defaultValue = "1") Integer current,
        @RequestParam(defaultValue = "10") Integer size,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) String category
    ) {
        Page<Income> page = new Page<>(current, size);
        QueryWrapper<Income> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", getCurrentUserId())
                   .eq("deleted", 0)
                   .orderByDesc("income_date");
        
        if (startDate != null && endDate != null) {
            queryWrapper.between("income_date", startDate, endDate);
        }
        
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq("category", category);
        }
        
        return Result.success(incomeService.page(page, queryWrapper));
    }

    @GetMapping("/range")
    public Result<List<Income>> listByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Result.success(incomeService.getByUserIdAndDateRange(
                getUserIdFromUsername(username), startDate, endDate));
    }

    @PostMapping
    public Result<Income> add(@RequestBody Income income) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        income.setUserId(getUserIdFromUsername(username));
        return Result.success(incomeService.add(income));
    }

    @PutMapping("/{id}")
    public Result<Income> update(@PathVariable Long id, @RequestBody Income income) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        income.setId(id);
        income.setUserId(getUserIdFromUsername(username));
        incomeService.updateById(income);
        return Result.success(income);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        incomeService.delete(id, getUserIdFromUsername(username));
        return Result.success();
    }

    private Long getUserIdFromUsername(String username) {
        // TODO: 实现从用户名获取用户ID的逻辑
        return 1L;
    }

    private Long getCurrentUserId() {
        // TODO: 实现获取当前用户ID的逻辑
        return 1L;
    }
} 