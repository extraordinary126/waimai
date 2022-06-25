package com.yuhao.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuhao.waimai.bean.Orders;

import javax.servlet.http.HttpSession;

public interface OrdersService extends IService<Orders> {

    void submit(Orders orders, HttpSession session);

    void orderAgain(Long id);
}
