package com.ggjcbgg.guguji.dto;


import com.ggjcbgg.guguji.entity.Dish;
import com.ggjcbgg.guguji.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装页面传输的对象
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
