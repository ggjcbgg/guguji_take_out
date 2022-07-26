package com.ggjcbgg.guguji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ggjcbgg.guguji.entity.Employee;
import com.ggjcbgg.guguji.mapper.EmployeeMapper;
import com.ggjcbgg.guguji.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * 员工的业务层：对员工进行增删改查等操作
 */
@Service //让spring管理,业务层
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    //继承Mybatis-plus中的ServiceImpl方法，实现EmployeeService接口
}
