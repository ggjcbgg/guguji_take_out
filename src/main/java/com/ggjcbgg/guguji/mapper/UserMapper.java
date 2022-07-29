package com.ggjcbgg.guguji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ggjcbgg.guguji.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
