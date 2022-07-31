package com.ggjcbgg.guguji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ggjcbgg.guguji.common.BaseContext;
import com.ggjcbgg.guguji.common.R;
import com.ggjcbgg.guguji.entity.ShoppingCart;
import com.ggjcbgg.guguji.mapper.ShoppingCartMapper;
import com.ggjcbgg.guguji.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 购物车业务层实现类
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    /**
     * 添加到购物车
     * @param shoppingCart
     * @return
     */
    public ShoppingCart add(ShoppingCart shoppingCart) {

        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //查询当前菜品或者套餐是否在购物车中，保证口味一样
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

        if(dishId != null){
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //queryWrapper.eq(shoppingCart.getDishFlavor()!=null,ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());

        ShoppingCart cartServiceOne = this.getOne(queryWrapper);

        if(cartServiceOne != null) {
            //如果已经存在，就在原来数量基础上加一
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            this.updateById(cartServiceOne);
        }else {
            //如果不存在，则添加到购物车，数量默认就是一
            if(shoppingCart.getNumber()==null){
                shoppingCart.setNumber(1);
            }
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);

            cartServiceOne = shoppingCart;
        }

        return cartServiceOne;
    }
}
