package com.ggjcbgg.guguji.controller;

import com.ggjcbgg.guguji.common.R;
import com.ggjcbgg.guguji.dto.DishDto;
import com.ggjcbgg.guguji.entity.Dish;
import com.ggjcbgg.guguji.service.DishFlavorService;
import com.ggjcbgg.guguji.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }


}
