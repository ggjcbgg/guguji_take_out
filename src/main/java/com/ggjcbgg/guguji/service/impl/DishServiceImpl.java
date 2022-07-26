package com.ggjcbgg.guguji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ggjcbgg.guguji.dto.DishDto;
import com.ggjcbgg.guguji.entity.Dish;
import com.ggjcbgg.guguji.entity.DishFlavor;
import com.ggjcbgg.guguji.mapper.DishMapper;
import com.ggjcbgg.guguji.service.DishFlavorService;
import com.ggjcbgg.guguji.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单业务层
 */
@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    @Transactional//事务注解
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();//菜品id

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();//saveBatch保存的是集合

        //给flavors中每一个dishid赋值
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);

    }
}
