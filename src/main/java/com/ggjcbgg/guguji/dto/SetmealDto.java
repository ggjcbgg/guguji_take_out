package com.ggjcbgg.guguji.dto;

import com.ggjcbgg.guguji.entity.Setmeal;
import com.ggjcbgg.guguji.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
