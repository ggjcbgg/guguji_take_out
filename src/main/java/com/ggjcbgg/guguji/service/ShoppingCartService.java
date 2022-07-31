package com.ggjcbgg.guguji.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ggjcbgg.guguji.common.R;
import com.ggjcbgg.guguji.entity.ShoppingCart;

/**
 * 购物车业务接口
 */
public interface ShoppingCartService extends IService<ShoppingCart> {

    /**
     * 添加到购物车
     * @param shoppingCart
     * @return
     */
    ShoppingCart add(ShoppingCart shoppingCart);
}
