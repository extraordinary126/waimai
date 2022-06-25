package com.yuhao.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuhao.waimai.bean.Setmeal;
import com.yuhao.waimai.dto.DishDto;
import com.yuhao.waimai.dto.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    void saveSetmealWithDish(SetmealDto setmealDto);

    void removeSetmealWithDish(List<Long> ids);

    //查询套餐及其对应的菜品 并回显到页面上
    SetmealDto getSetmealWithDishes(Long id);

    //更新套餐及其对应绑定的菜品
    void updateSetmealWithDishes(SetmealDto setmealDto);
}
