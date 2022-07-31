package com.ggjcbgg.guguji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ggjcbgg.guguji.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
