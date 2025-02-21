package com.family.assistant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.family.assistant.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
} 