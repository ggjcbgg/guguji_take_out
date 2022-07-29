package com.ggjcbgg.guguji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ggjcbgg.guguji.common.CustomException;
import com.ggjcbgg.guguji.common.R;
import com.ggjcbgg.guguji.dto.DishDto;
import com.ggjcbgg.guguji.entity.*;
import com.ggjcbgg.guguji.service.CategoryService;
import com.ggjcbgg.guguji.service.DishFlavorService;
import com.ggjcbgg.guguji.service.DishService;
import com.ggjcbgg.guguji.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;//菜品业务

    @Autowired
    private DishFlavorService dishFlavorService;//菜品口味业务

    @Autowired
    private CategoryService categoryService;//分页业务

    @Autowired
    private SetmealDishService setmealDishService;//菜品套餐业务

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        //排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝，忽略某个对象不拷贝,不拷贝records是因为List<T> records; T的种类不一样，不能进行拷贝，所以只拷贝分页的页数和大小等信息
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();//获取记录列表

        //stream创建流进行遍历每个元素，map对数据进行操作映射,item就是遍历的每个记录，->之后的方法就是详细处理,collect将流转化成列表
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //因为dishDto继承了Dish，所以能够进行拷贝
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//得到分类id
            System.out.println("---------"+categoryId);
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();//获得分类名
                dishDto.setCategoryName(categoryName);//给dishDto封装对象设置分类名
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);//把查询的结果值赋给dishDtoPage的records属性

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){//@RequestBody接收json封装的对象
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        //构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        //根据名称查找菜品
//        queryWrapper.like(dish.getName()!=null,Dish::getName,dish.getName());
//        //添加查询条件，1是查询正在售卖的(起售状态)
//        queryWrapper.eq(Dish::getStatus,1);
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);//执行查询
//
//        return R.success(list);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //根据名称查找菜品
        queryWrapper.like(dish.getName()!=null,Dish::getName,dish.getName());
        //添加查询条件，1是查询正在售卖的(起售状态)
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);//执行查询

        //创建DishDto对象封装口味信息
        List<DishDto> dishDtoList = null;

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //根据菜品id查询口味信息
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper1);
            //拷贝dish中的信息
            BeanUtils.copyProperties(item,dishDto);
            //设置dishDto中的口味信息
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }


    /**
     * 根据id对菜品进行停售
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> down(@RequestParam List<Long> ids){
        //条件构造器
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        //添加更新条件
        updateWrapper.in(Dish::getId,ids);
        updateWrapper.set(Dish::getStatus,0);
        //执行更新
        dishService.update(updateWrapper);

        return R.success("停售成功");
    }

    /**
     * 根据id对菜品进行启售
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> up(@RequestParam List<Long> ids){
        //条件构造器
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        //添加更新条件
        updateWrapper.in(Dish::getId,ids);
        updateWrapper.set(Dish::getStatus,1);
        //执行更新
        dishService.update(updateWrapper);

        return R.success("起售成功");
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        if(ids.isEmpty()){
            return R.error("请勾选要删除的菜品");
        }
        log.info("ids:{}",ids);

        //查询状态，确定是否可以删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);
        //查找正在售卖的菜品数量
        int count = dishService.count(queryWrapper);
        if(count>0) {
            //如果不能删除，抛出一个业务异常
            throw new CustomException("菜品正在售卖中，不能删除");
        }

        //查询套餐中是否在出售
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        lambdaQueryWrapper.in(SetmealDish::getDishId,ids);
        //查找套餐是否有该菜品
        int count1 = setmealDishService.count(lambdaQueryWrapper);
        if(count1>0) {
            //如果不能删除，抛出一个业务异常
            throw new CustomException("菜品正在套餐中，不能删除");
        }

        //如果可以删除，先删除菜品表中的数据
        dishService.removeByIds(ids);

        return R.success("菜品数据删除成功");
    }

}
