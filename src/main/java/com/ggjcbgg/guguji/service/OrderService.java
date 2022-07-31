package com.ggjcbgg.guguji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ggjcbgg.guguji.entity.Orders;

/**
 * 订单业务接口
 */
public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    void submit(Orders orders);
}
