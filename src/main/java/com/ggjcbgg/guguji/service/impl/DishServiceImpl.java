package com.ggjcbgg.guguji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ggjcbgg.guguji.dto.DishDto;
import com.ggjcbgg.guguji.entity.Dish;
import com.ggjcbgg.guguji.entity.DishFlavor;
import com.ggjcbgg.guguji.mapper.DishMapper;
import com.ggjcbgg.guguji.service.DishFlavorService;
import com.ggjcbgg.guguji.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
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

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        //拷贝dish的信息到dishDto中
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();//创建条件构造器
        //添加where条件
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        //dishDto添加口味属性
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜品和口味
     * @param dishDto
     */
    @Transactional //添加事务
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish菜品表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应的口味数据  dish_flavor表的delete操作
        //创建条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //根据dishId删除口味数据
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据  dish_flavor表的insert操作
        //获取菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        //给每一个dishFlavor的dishId赋值,然后对流进行列表排序
        flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return dishDto;
        }).collect(Collectors.toList());

        //批量保存
        dishFlavorService.saveBatch(flavors);

    }
}
