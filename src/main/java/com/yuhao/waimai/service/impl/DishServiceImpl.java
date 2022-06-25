package com.yuhao.waimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuhao.waimai.bean.*;
import com.yuhao.waimai.common.R;
import com.yuhao.waimai.dto.DishDto;
import com.yuhao.waimai.dto.SetmealDto;
import com.yuhao.waimai.mapper.DishMapper;
import com.yuhao.waimai.service.CategoryService;
import com.yuhao.waimai.service.DishFlavorService;
import com.yuhao.waimai.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Resource
    private DishFlavorService dishFlavorService;

    @Resource
    private DishService dishService;


    @Override
    @Transactional
    public void saveDishWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到dish表
        this.save(dishDto);
        //菜品的id  后面赋给口味表 因为DishFlavor类里有ID属性 手动给一下关联的菜品ID
        Long dishId = dishDto.getId();

        //保存菜品的口味数据到菜品口味表 dish_flavor
        List<DishFlavor> flavors = dishDto.getFlavors();

        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavors);
    }

    //    //菜品的修改  根据Id查询菜品信息以及口味
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        //获得菜品Dish对象
        Dish dish = this.getById(id);

        //将菜品对象拷贝到数据传输对象DTO dishDto
        BeanUtils.copyProperties(dish,dishDto);

        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据id查出 该菜品id对应的口味
        lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(lambdaQueryWrapper);

        //设置DishDto的口味
        dishDto.setFlavors(list);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateDishWithFlavors(DishDto dishDto) {

            Long id = dishDto.getId();
            //更新对应id 的Dish
            dishService.updateById(dishDto);
           //删除对应id 的DishFlavor
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
            dishFlavorService.remove(lambdaQueryWrapper);

            //获取传过来的实体类的口味Dishflavor
            List<DishFlavor> flavors = dishDto.getFlavors();
            //给选择的这些口味赋上Dish菜品的id

            //否则         //java.sql.SQLException: Field 'dish_id' doesn't have a default value
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(id);
            }
            //插入菜品口味DishFlavors
            dishFlavorService.saveBatch(flavors);
        }
}
