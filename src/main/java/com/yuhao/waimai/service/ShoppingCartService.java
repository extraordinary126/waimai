package com.yuhao.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuhao.waimai.bean.ShoppingCart;

public interface ShoppingCartService extends IService<ShoppingCart> {

    void clean();
}
