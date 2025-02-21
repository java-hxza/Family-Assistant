package com.family.assistant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.family.assistant.entity.Income;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface IncomeMapper extends BaseMapper<Income> {
    
    @Select("SELECT SUM(amount) FROM income WHERE user_id = #{userId} " +
            "AND income_date BETWEEN #{startDate} AND #{endDate}")
    BigDecimal sumByDateRange(@Param("startDate") String startDate,
                             @Param("endDate") String endDate,
                             @Param("userId") Long userId);

    @Select("SELECT category, SUM(amount) as total FROM income " +
            "WHERE user_id = #{userId} AND income_date BETWEEN #{startDate} AND #{endDate} " +
            "AND deleted = 0 GROUP BY category")
    List<Map<String, Object>> sumByCategory(@Param("startDate") String startDate, 
                                          @Param("endDate") String endDate,
                                          @Param("userId") Long userId);

    List<Income> listByDateRange(@Param("startDate") String startDate, 
                               @Param("endDate") String endDate,
                               @Param("userId") Long userId);
} 