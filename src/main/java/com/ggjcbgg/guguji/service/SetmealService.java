package com.ggjcbgg.guguji.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ggjcbgg.guguji.dto.SetmealDto;
import com.ggjcbgg.guguji.entity.Setmeal;

/**
 * 套餐业务层接口
 */
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);
}
