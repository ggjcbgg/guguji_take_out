package com.ggjcbgg.guguji.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ggjcbgg.guguji.dto.SetmealDto;
import com.ggjcbgg.guguji.entity.Setmeal;

import java.util.List;

/**
 * 套餐业务层接口
 */
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    void removeWithDish(List<Long>ids);

    /**
     * 根据套餐id查询套餐信息和菜品关联信息
     * @param id
     * @return
     */
    SetmealDto getByIdWithDish(Long id);

    /**
     * 更新套餐，同时更新套餐信息和菜品关联信息
     * @param setmealDto
     */
    void updateWithDish(SetmealDto setmealDto);
}
