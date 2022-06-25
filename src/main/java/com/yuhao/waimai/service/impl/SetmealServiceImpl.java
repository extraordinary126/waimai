package com.yuhao.waimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuhao.waimai.bean.Setmeal;
import com.yuhao.waimai.bean.SetmealDish;
import com.yuhao.waimai.common.CustomException;
import com.yuhao.waimai.common.R;
import com.yuhao.waimai.dto.DishDto;
import com.yuhao.waimai.dto.SetmealDto;
import com.yuhao.waimai.mapper.SetmealMapper;
import com.yuhao.waimai.service.SetmealDishService;
import com.yuhao.waimai.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {


    @Resource
    private SetmealDishService setmealDishService;

    //删除套餐 同时删除关联的菜品
    @Override
    @Transactional
    public void removeSetmealWithDish(List<Long> ids) {
        //select count(*) from setmeal where id in (1,2,3) and status = 1;
        //查询套餐状态 确定是否可以删除
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);
        //如果查询的数量 >0 则说明传过来的套餐包含在售卖的
        if (this.count(lambdaQueryWrapper) > 0){
            throw new CustomException("套餐正在售卖中!不可以删除");
        }
        //到这说明可以删除 先删除套餐表 setmeal
        this.removeByIds(ids);
        //删除 套餐菜品关系表 setmeal_dish
        // Delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper);

    }

    @Override
    @Transactional
    public void saveSetmealWithDish(SetmealDto setmealDto) {
            //先保存一部分基本信息
            this.save(setmealDto);
            Long id = setmealDto.getId();
            //取出套餐和菜品的关联信息集合
            List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
            //给这些集合赋上套餐的ID
            for (SetmealDish dish : setmealDishList) {
                dish.setSetmealId(id);
            }
            //保存套餐菜品关联信息    Setmeal_Dish表
            setmealDishService.saveBatch(setmealDishList);
        }

    @Override
    @Transactional
    //查询 套餐和其对应的菜品 并回显到页面上吧
    public SetmealDto getSetmealWithDishes(Long id) {
        //查询id 对应的套餐
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        //从套餐菜品关系表 Setmeal_Dish中查询 套餐所对应的菜品集合 List
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishList = setmealDishService.list(lambdaQueryWrapper);

        //将套餐对应的菜品存入SetmealDto对象中返回
        setmealDto.setSetmealDishes(setmealDishList);
        return setmealDto;
    }

    //更新套餐及其对应绑定的菜品
    @Override
    @Transactional
    public void updateSetmealWithDishes(SetmealDto setmealDto) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDto,setmeal);
        //先更新一部分
        this.updateById(setmeal);

        //再更新对应的菜品   先删除再添加  套餐的id  就是  套餐菜品表setmeal_dish 里的 Setmeal_id (套餐id)字段

        //删除套餐菜品表中  对应套餐id 的菜品
        Long id = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        setmealDishService.remove(lambdaQueryWrapper);

        //重新添加
        List<SetmealDish> setmealDishesList = setmealDto.getSetmealDishes();

        //java.sql.SQLException: Field 'setmeal_id' doesn't have a default value
        // 菜品没有对应的 套餐id 取出来赋一下id
        for (SetmealDish setmealDish : setmealDishesList) {
            //给setmealDish 套餐菜品对应表 赋上菜品对应的套餐的id
            setmealDish.setSetmealId(id);
        }
        setmealDishService.saveBatch(setmealDishesList);
    }
}
