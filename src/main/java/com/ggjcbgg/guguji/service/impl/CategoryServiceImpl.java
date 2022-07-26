package com.ggjcbgg.guguji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ggjcbgg.guguji.common.CustomException;
import com.ggjcbgg.guguji.entity.Category;
import com.ggjcbgg.guguji.entity.Dish;
import com.ggjcbgg.guguji.entity.Setmeal;
import com.ggjcbgg.guguji.mapper.CategoryMapper;
import com.ggjcbgg.guguji.service.CategoryService;
import com.ggjcbgg.guguji.service.DishService;
import com.ggjcbgg.guguji.service.SetmealService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 分类的业务层：对分类进行增删改查
 */
@Service  //继承Mybatis-plus中的ServiceImpl方法，实现EmployeeService接口
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        //条件构造器,where
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);//执行查询

        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        if (count1>0){
            //已经关联了菜品，抛出一个业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);//执行查询
        if(count2>0){
            //已经关联了套餐，抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        //正常删除分类
        super.removeById(id);
    }

}
