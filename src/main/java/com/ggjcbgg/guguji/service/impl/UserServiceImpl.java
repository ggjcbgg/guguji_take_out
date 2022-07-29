package com.ggjcbgg.guguji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ggjcbgg.guguji.entity.User;
import com.ggjcbgg.guguji.mapper.UserMapper;
import com.ggjcbgg.guguji.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户信息业务层实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
