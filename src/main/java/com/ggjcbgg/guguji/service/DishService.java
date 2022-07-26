package com.ggjcbgg.guguji.service;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ggjcbgg.guguji.dto.DishDto;
import com.ggjcbgg.guguji.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜单业务层接口
 */
public interface DishService extends IService<Dish> {

    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表
    public void saveWithFlavor(DishDto dishDto);

}
