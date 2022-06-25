package com.yuhao.waimai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuhao.waimai.bean.OrderDetail;
import com.yuhao.waimai.mapper.OrderDetailMapper;
import com.yuhao.waimai.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
