package com.yuhao.waimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuhao.waimai.bean.Category;
import com.yuhao.waimai.bean.Dish;
import com.yuhao.waimai.bean.Setmeal;
import com.yuhao.waimai.common.CustomException;
import com.yuhao.waimai.common.R;
import com.yuhao.waimai.mapper.CategoryMapper;
import com.yuhao.waimai.service.CategoryService;
import com.yuhao.waimai.service.DishService;
import com.yuhao.waimai.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {

    @Resource
    private DishService dishService;
    @Resource
    private SetmealService setmealService;



    //    //自定义删除方法 根据id删除分类 在分类关联了菜品或套餐时不能删除
    @Override
    public void remove(Long id) {
        //当前分类如果关联了套餐  抛出一个异常
        //        //查询该id在该表中是否被某套餐关联 如果关联数量>0 则不删除
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper  = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if (setmealCount > 0){
            //已经关联了套餐 抛出异常
            //然后在全局异常处理器中捕获异常
            throw new CustomException("该分类关联了套餐 不能删除");
        }

        //当前分类如果关联了菜品 抛出一个异常
        //查询该id在该表中是否被某菜品关联 如果关联数量>0 则不删除
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        if (dishCount > 0){
            //已经关联了菜品 抛出异常
            //然后在全局异常处理器中捕获异常
           throw new CustomException("该分类关联了菜品 不能删除");
        }

        //都没有关联 正常删除
        /*categoryMapper.deleteById(id);*/
        super.removeById(id);
    }


}
