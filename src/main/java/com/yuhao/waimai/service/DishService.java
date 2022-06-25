package com.yuhao.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuhao.waimai.bean.Dish;
import com.yuhao.waimai.dto.DishDto;


public interface DishService extends IService<Dish> {

    //新增菜品 同时插入菜品所对应的口味数据  需要操作两张表 dish  和 dish_flavor
    void saveDishWithFlavor(DishDto dishDto);

    //菜品的修改  根据Id查询菜品信息以及口味
    DishDto getByIdWithFlavor(Long id);

    void updateDishWithFlavors(DishDto dishDto);

}
