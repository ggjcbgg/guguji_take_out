package com.ggjcbgg.guguji.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ggjcbgg.guguji.entity.AddressBook;
import com.ggjcbgg.guguji.mapper.AddressBookMapper;
import com.ggjcbgg.guguji.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * 地址信息接口实现类
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
