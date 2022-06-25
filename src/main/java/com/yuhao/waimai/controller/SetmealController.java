package com.yuhao.waimai.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.waimai.bean.Category;
import com.yuhao.waimai.bean.Dish;
import com.yuhao.waimai.bean.Setmeal;
import com.yuhao.waimai.bean.SetmealDish;
import com.yuhao.waimai.common.R;
import com.yuhao.waimai.dto.DishDto;
import com.yuhao.waimai.dto.SetmealDto;
import com.yuhao.waimai.service.CategoryService;
import com.yuhao.waimai.service.DishService;
import com.yuhao.waimai.service.SetmealDishService;
import com.yuhao.waimai.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/*套餐Controller*/
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Resource
    private SetmealService setmealService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private SetmealDishService setmealDishService;

    @Resource
    private DishService dishService;

    /* 套餐的分页查询
    请求网址: http://localhost:8083/setmeal/page?page=1&pageSize=10&name=wqqweqw
    请求方法: GET */
    @GetMapping("/page")
    public R<Page> pageQuery(int page, int pageSize, String name){
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null,Setmeal::getName,name);
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo, lambdaQueryWrapper);

        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
        List<SetmealDto> list = new ArrayList<>();
        List<Setmeal> recordsList = pageInfo.getRecords();
        for (Setmeal setmeal : recordsList) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);
            Long categoryId = setmeal.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            list.add(setmealDto);
        }
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    /*  添加套餐页面中 的保存套餐
    * 请求网址: http://localhost:8083/setmeal
      请求方法: POST
    * */
    @PostMapping
        public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto){
            //自定义的  同时保存套餐信息 和套餐菜品的关联信息
            setmealService.saveSetmealWithDish(setmealDto);
            return R.success("添加套餐成功!");
        }

        //套餐的删除
        /*请求网址: http://localhost:8083/setmeal?ids=1539444093275422722
        请求方法: DELETE*/
    @DeleteMapping              //
    public R<String> deleteSetmeal(@RequestParam List<Long> ids){
        setmealService.removeSetmealWithDish(ids);
        return R.success("删除成功!");
       /* for (Long id : ids) {
            setmealService.removeById(id);
        }
        return R.success("删除成功!");*/
    }
    //套餐的状态更改 启售停售
    /*请求网址: http://localhost:8083/setmeal/status/0?ids=1539443732712050689
      请求方法: POST*/
    @PostMapping("/status/{status}")
    public R<String> updateStatus(Long[] ids,@PathVariable int status){
        for (Long id : ids) {
            LambdaUpdateWrapper<Setmeal> lambdaUpdateWrapper= new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.set(Setmeal::getStatus,status);
            lambdaUpdateWrapper.eq(Setmeal::getId,id);
            setmealService.update(lambdaUpdateWrapper);
        }
        return R.success("更新状态成功!");
    }

    //点击修改 将数据回显在页面上
    /*请求网址: http://localhost:8083/setmeal/1539521923808006146
    请求方法: GET*/
    @GetMapping("/{id}")
    public R<SetmealDto> getSetMeal(@PathVariable Long id){
        SetmealDto setmealWithDishes = setmealService.getSetmealWithDishes(id);
        return R.success(setmealWithDishes);
    }

    //更新套餐
    /*请求网址: http://localhost:8083/setmeal
      请求方法: PUT*/
    @PutMapping
    public R<String> updateSetmeal(@RequestBody SetmealDto setmealDto){
        setmealService.updateSetmealWithDishes(setmealDto);
        return R.success("修改成功!");
    }

    /*请求网址: http://localhost:8083/setmeal/list?categoryId=1413386191767674881&status=1
    请求方法: GET*/
    @GetMapping("/list")
    public R<List<Setmeal>> getSetmealInFront(Setmeal setmeal){

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        setmealLambdaQueryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,1);
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(setmealLambdaQueryWrapper);
        return R.success(list);
    }
    //点击套餐查看里面的菜品
    /*请求网址: http://localhost:8083/setmeal/dish/1539521923808006146
    请求方法: GET*/
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> dish(@PathVariable("id") Long setmealId){
        //获取套餐里的所有菜品数据
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(setmealId != null,SetmealDish::getSetmealId,setmealId);
        List<SetmealDish> setmealDishList = setmealDishService.list(lambdaQueryWrapper);

        List<DishDto> dishDtoList = new ArrayList<>();
        for (SetmealDish setmealDish : setmealDishList) {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(setmealDish, dishDto);
            //这里是为了把套餐中的菜品的基本信息填充到dto中，比如菜品描述，菜品图片等菜品的基本信息
            Long dishId = setmealDish.getDishId();
            Dish dish = dishService.getById(dishId);
            BeanUtils.copyProperties(dish, dishDto);
            dishDtoList.add(dishDto);
        }

        return R.success(dishDtoList);
    }

    /*请求网址: http://localhost:8083/setmeal/list?categoryId=1413386191767674881&status=1
    请求方法: GET*/
    // 废案  在套餐大类 中展示菜品
    /*@GetMapping("/list")
        public R<List<SetmealDish>> getSetmealInFront(Setmeal setmeal){

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        setmealLambdaQueryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,1);
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        Setmeal setmeal1 = setmealService.getOne(setmealLambdaQueryWrapper);

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Long setmealId = setmeal1.getId();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper);
        return R.success(list);
    }*/
}
