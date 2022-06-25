package com.yuhao.waimai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuhao.waimai.bean.AddressBook;
import com.yuhao.waimai.mapper.AddressBookMapper;
import com.yuhao.waimai.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
