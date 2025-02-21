package com.family.assistant.controller;

import com.family.assistant.common.Result;
import com.family.assistant.entity.Expense;
import com.family.assistant.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/list")
    public Result<IPage<Expense>> list(
        @RequestParam(defaultValue = "1") Integer current,
        @RequestParam(defaultValue = "10") Integer size,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) String category
    ) {
        Page<Expense> page = new Page<>(current, size);
        QueryWrapper<Expense> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", getCurrentUserId())
                   .eq("deleted", 0)
                   .orderByDesc("expense_date");
        
        if (startDate != null && endDate != null) {
            queryWrapper.between("expense_date", startDate, endDate);
        }
        
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq("category", category);
        }
        
        return Result.success(expenseService.page(page, queryWrapper));
    }

    @GetMapping("/range")
    public Result<List<Expense>> listByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Result.success(expenseService.getByUserIdAndDateRange(
                getUserIdFromUsername(username), startDate, endDate));
    }

    @PostMapping
    public Result<Expense> add(@RequestBody Expense expense) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        expense.setUserId(getUserIdFromUsername(username));
        return Result.success(expenseService.add(expense));
    }

    @PutMapping("/{id}")
    public Result<Expense> update(@PathVariable Long id, @RequestBody Expense expense) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        expense.setId(id);
        expense.setUserId(getUserIdFromUsername(username));
        expenseService.updateById(expense);
        return Result.success(expense);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        expenseService.delete(id, getUserIdFromUsername(username));
        return Result.success();
    }

    // 这个方法需要根据实际情况实现
    private Long getUserIdFromUsername(String username) {
        // TODO: 实现从用户名获取用户ID的逻辑
        return 1L;
    }

    private Long getCurrentUserId() {
        // TODO: 实现获取当前用户ID的逻辑
        return 1L;
    }
} 