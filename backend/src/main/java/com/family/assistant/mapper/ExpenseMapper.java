package com.family.assistant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.family.assistant.entity.Expense;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ExpenseMapper extends BaseMapper<Expense> {
    
    @Select("SELECT SUM(amount) FROM expense WHERE user_id = #{userId} " +
            "AND expense_date BETWEEN #{startDate} AND #{endDate}")
    BigDecimal sumByDateRange(@Param("startDate") String startDate,
                             @Param("endDate") String endDate,
                             @Param("userId") Long userId);

    @Select("SELECT category, SUM(amount) as total FROM expense " +
            "WHERE user_id = #{userId} AND expense_date BETWEEN #{startDate} AND #{endDate} " +
            "AND deleted = 0 GROUP BY category")
    List<Map<String, Object>> sumByCategory(@Param("startDate") String startDate, 
                                          @Param("endDate") String endDate,
                                          @Param("userId") Long userId);

    List<Expense> listByDateRange(@Param("startDate") String startDate, 
                                @Param("endDate") String endDate,
                                @Param("userId") Long userId);
} 