package com.ggjcbgg.guguji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ggjcbgg.guguji.common.R;
import com.ggjcbgg.guguji.dto.SetmealDto;
import com.ggjcbgg.guguji.entity.Category;
import com.ggjcbgg.guguji.entity.Setmeal;
import com.ggjcbgg.guguji.service.CategoryService;
import com.ggjcbgg.guguji.service.SetmealDishService;
import com.ggjcbgg.guguji.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;//套餐业务

    @Autowired
    private SetmealDishService setmealDishService;//套餐菜品业务

    @Autowired
    private CategoryService categoryService;//分类业务

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}",setmealDto);

        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行like模糊查询
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //添加排序条件，根据更新实际降序排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行查询
        setmealService.page(pageInfo,queryWrapper);

        //对象拷贝，records不需要拷贝，两个对象records的属性类型不一致
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        //获取查询到records记录
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list =  records.stream().map((item) -> {
            SetmealDto setmealDto =new SetmealDto();
            //拷贝setmeal数据到setmealDto中
            BeanUtils.copyProperties(item,setmealDto);
            //分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                //获取分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        //设置records数据
        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        if(ids.isEmpty()){
            return R.error("请勾选要删除的套餐");
        }
        log.info("ids:{}",ids);

        setmealService.removeWithDish(ids);

        return R.success("套餐数据删除成功");
    }

    /**
     * 根据id查询套餐信息和菜品关联信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        log.info("查询的套餐id为：{}",id);

        SetmealDto setmealDto = setmealService.getByIdWithDish(id);

        return R.success(setmealDto);

    }

    /**
     * 修改套餐和菜品关联信息
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}",setmealDto);

        setmealService.updateWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /**
     * 根据id对套餐进行停售
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> down(@RequestParam List<Long> ids){
        //条件构造器
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        //添加更新条件
        updateWrapper.in(Setmeal::getId,ids);
        updateWrapper.set(Setmeal::getStatus,0);
        //执行更新
        setmealService.update(updateWrapper);

        return R.success("停售成功");
    }

    /**
     * 根据id对套餐进行启售
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> up(@RequestParam List<Long> ids){
        //条件构造器
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        //添加更新条件
        updateWrapper.in(Setmeal::getId,ids);
        updateWrapper.set(Setmeal::getStatus,1);
        //执行更新
        setmealService.update(updateWrapper);

        return R.success("起售成功");
    }

    /**
     * 根据分类id查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal ){
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null ,Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null ,Setmeal::getStatus, setmeal.getStatus());

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }

}

