package com.family.assistant.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("income")
public class Income {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private String category;
    private String description;
    private LocalDateTime incomeDate;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
} 