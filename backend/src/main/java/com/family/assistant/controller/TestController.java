package com.family.assistant.controller;

import cn.hutool.core.date.DateUtil;
import com.family.assistant.common.Result;
import com.family.assistant.util.TestDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private TestDataGenerator testDataGenerator;

    @PostMapping("/generate")
    public Result<?> generateTestData(@RequestParam(required = false) Integer year) {
        if (year == null) {
            year = DateUtil.year(new Date());
        }
        try {
            testDataGenerator.generateTestData(year, 1L); // 使用默认用户ID 1
            return Result.success("测试数据生成成功");
        } catch (Exception e) {
            return Result.error("测试数据生成失败：" + e.getMessage());
        }
    }
} 