package com.ggjcbgg.guguji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ggjcbgg.guguji.entity.Category;

/**
 * 业务层接口，继承MybatisPlus中的通用接口
 */
public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
