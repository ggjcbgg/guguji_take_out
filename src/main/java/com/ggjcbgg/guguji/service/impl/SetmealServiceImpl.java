package com.ggjcbgg.guguji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ggjcbgg.guguji.common.CustomException;
import com.ggjcbgg.guguji.common.R;
import com.ggjcbgg.guguji.dto.SetmealDto;
import com.ggjcbgg.guguji.entity.Category;
import com.ggjcbgg.guguji.entity.DishFlavor;
import com.ggjcbgg.guguji.entity.Setmeal;
import com.ggjcbgg.guguji.entity.SetmealDish;
import com.ggjcbgg.guguji.mapper.SetmealMapper;
import com.ggjcbgg.guguji.service.CategoryService;
import com.ggjcbgg.guguji.service.SetmealDishService;
import com.ggjcbgg.guguji.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐业务层
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Transactional //添加事务
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);

        //获取套餐菜品关系
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //设置每一个套餐菜品信息的套餐id,将流封装成list
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作
        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        //查找正在售卖的套餐数量
        int count = this.count(queryWrapper);
        if(count>0) {
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);

        //条件构造器
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        //删除关系表中的数据
        setmealDishService.remove(lambdaQueryWrapper);

    }

    /**
     * 根据套餐id查询套餐信息和菜品关联信息
     * @param id
     * @return
     */
    @Transactional
    public SetmealDto getByIdWithDish(Long id) {
        //根据套餐id查询setmeal套餐信息
        Setmeal setmeal = this.getById(id);
        //根据套餐id查询套餐菜品关联信息
        //条件构造器
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        //执行查询，返回查询结果集
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        //根据分类id查询分类信息
        Category category = categoryService.getById(setmeal.getCategoryId());
        //创建SetmealDto对象
        SetmealDto setmealDto = new SetmealDto();
        //将setmeal数据拷贝到setmealDto中
        BeanUtils.copyProperties(setmeal,setmealDto);
        //设置分类名称和菜品关联信息
        setmealDto.setSetmealDishes(list);
        setmealDto.setCategoryName(category.getName());
        return setmealDto;
    }

    /**
     * 更新套餐，同时更新套餐信息和菜品关联信息
     * @param setmealDto
     */
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        //更新套餐的基本信息，操作setmeal，并执行update操作
        this.updateById(setmealDto);

        //清理当前套餐对应的菜品数据  delete操作
        //创建条件构造器
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        //根据dishId删除口味数据
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        //获取套餐菜品关系
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //设置每一个套餐信息的套餐id，将流封装成list
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //更新套餐和菜品的关联信息,插入
        setmealDishService.saveBatch(setmealDishes);

    }


}
