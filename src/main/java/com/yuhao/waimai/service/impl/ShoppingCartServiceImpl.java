package com.yuhao.waimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuhao.waimai.bean.ShoppingCart;
import com.yuhao.waimai.common.BaseContext;
import com.yuhao.waimai.common.R;
import com.yuhao.waimai.mapper.ShoppingCartMapper;
import com.yuhao.waimai.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {


    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        this.remove(lambdaQueryWrapper);
    }
}
