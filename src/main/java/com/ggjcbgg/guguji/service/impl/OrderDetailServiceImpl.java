package com.ggjcbgg.guguji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ggjcbgg.guguji.entity.OrderDetail;
import com.ggjcbgg.guguji.mapper.OrderDetailMapper;
import com.ggjcbgg.guguji.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * 订单明细业务实现类
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
