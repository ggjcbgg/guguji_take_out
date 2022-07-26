package com.ggjcbgg.guguji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ggjcbgg.guguji.entity.Setmeal;
import com.ggjcbgg.guguji.mapper.SetmealMapper;
import com.ggjcbgg.guguji.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 套餐业务层
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
}
