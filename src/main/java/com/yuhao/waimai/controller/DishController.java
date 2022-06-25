package com.yuhao.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.waimai.bean.Category;
import com.yuhao.waimai.bean.Dish;
import com.yuhao.waimai.bean.DishFlavor;
import com.yuhao.waimai.common.R;
import com.yuhao.waimai.dto.DishDto;
import com.yuhao.waimai.service.CategoryService;
import com.yuhao.waimai.service.DishFlavorService;
import com.yuhao.waimai.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Resource
    DishService dishService;
    @Resource
    DishFlavorService dishFlavorService;
    @Resource
    CategoryService categoryService;

    @PostMapping      //添加菜品和对应的口味功能
    public R<String> saveDish(@RequestBody DishDto dishDto){
        dishService.saveDishWithFlavor(dishDto);
        return R.success("添加成功!");
    }

/*
    请求网址: http://localhost:8083/dish/page?page=1&pageSize=10
    请求方法: GET
*/
    @GetMapping("/page")
    public R<Page> pageQuery(int page, int pageSize,String name){
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);

        Page<DishDto> dtoPageInfo = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //条件
        lambdaQueryWrapper.like(name != null ,Dish::getName,name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,lambdaQueryWrapper);
        //对象的拷贝
                    //protected List<T> records;
        // 两个分页构造器Page不一样的是records集合,旧的pageInfo里是categoryId 新的dtoPageInfo对象里面还放了categoryName 所以除了records 之外 都拷贝
        BeanUtils.copyProperties(pageInfo,dtoPageInfo,"records");
        //获取records 自己处理
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = new ArrayList<>();
        // Dish中 是 categoryId    DishDto 中是 categoryName
        // 遍历 records集合 将里面的categoryId  变成  categoryName  添加到 disDto中
        for (Dish dish : records) {
            //new一个 DisDto对象
            DishDto dishDto = new DishDto();
            //将List集合: records 中取出的dish对象拷贝到new的DisDto对象
            BeanUtils.copyProperties(dish,dishDto);
            //从取出的Dish对象中得到菜品种类id
            Long categoryId = dish.getCategoryId();
            //通过取出的id获得 菜品种类category对象
            Category categoryObj = categoryService.getById(categoryId);
            if (categoryObj != null){
                //通过category对象获得菜品种类的名称
                String categoryObjName = categoryObj.getName();
                //设置 disDto对象的categoryName属性
                dishDto.setCategoryName(categoryObjName);
            }

            //将disDto对象添加到List集合中
            list.add(dishDto);
        }
           /* List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId(); //分类的id
            //根据分类id查出分类对象
            Category categoryObj = categoryService.getById(categoryId);
            String categoryObjName = categoryObj.getName();
            dishDto.setCategoryName(categoryObjName);
            return dishDto;
        }).collect(Collectors.toList());*/
        // 设置 分页构造器的属性
        dtoPageInfo.setRecords(list);
        return R.success(dtoPageInfo);
    }


    //在修改菜品页面进行回显
    /*请求网址: http://localhost:8083/dish/1397849739276890114
      请求方法: GET*/
    @GetMapping("/{id}")
    public R<DishDto> getDishInUpdatePage(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    //修改菜品
    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto){
        // 对于dish普通字段 直接更新    对于口味字段dishFlavor 先删除对应id 的dishflavor字段 再根据传过来的DishDto重新添加
        dishService.updateDishWithFlavors(dishDto);
        return R.success("修改成功!");
    }
    //禁售菜品 / 启售菜品
    /*请求网址: http://localhost:8083/dish/status/0?ids=1539129077108121601
    请求方法: POST*/
    @PostMapping("/status/{status}")
    public R<String> changDishStatus(@PathVariable int status, Long[] ids){
        for (Long id : ids) {
            LambdaUpdateWrapper<Dish> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            //注意是set 设置状态
            lambdaUpdateWrapper.set(Dish::getStatus,status);
            // 条件是等于 id 的
            lambdaUpdateWrapper.eq(Dish::getId,id);
            dishService.update(lambdaUpdateWrapper);
        }
        return R.success("更新菜品状态成功!");
    }

    /*请求网址: http://localhost:8083/dish?ids=1539129077108121601
      请求方法: DELETE*/
    @DeleteMapping
    public R<String> deleteDish(Long[] ids){

        for (long id : ids) {
            if (dishService.getById(id).getStatus() == 0) {
                dishService.removeById(id);
            }else return R.success("售卖中的必须先停售再删除!!");
        }
        return R.success("删除成功");
    }

    /*  添加套餐的添加菜品页面中展示菜品的数据
    请求网址: http://localhost:8083/dish/list?categoryId=1397844263642378242
    请求方法: GET*/
   /* @GetMapping("/list")
    public R<List<Dish>> queryDish(Dish dish){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //查询categoryId为传过来的categoryID的菜品
        lambdaQueryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //状态为1是启售
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);
        return R.success(list);
    }*/
    @GetMapping("/list")
    public R<List<DishDto>> queryDish(Dish dish){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //查询categoryId为传过来的categoryID的菜品
        lambdaQueryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //状态为1是启售
        lambdaQueryWrapper.eq(Dish::getStatus,1);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        //查询出菜品集合
        List<Dish> list = dishService.list(lambdaQueryWrapper);



        List<DishDto> dtoList = new ArrayList<>();
        for (Dish dish1 : list) {
            DishDto dishDto = new DishDto();
            //复制对象 将不包含口味数据的Dish字段 赋给 包含口味数据的Dto字段
            BeanUtils.copyProperties(dish1,dishDto);
            //根据菜品的id 查出菜品的口味  将查出来的口味集合放到新的DTO对象中 再把DTO对象存入新的数组
            Long dishId = dish1.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //select * from dish_flavor where dish id = ?;
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            //将查出来的口味集合放到新的DTO对象中
            dishDto.setFlavors(dishFlavorList);
            //再把DTO对象存入新的数组
            dtoList.add(dishDto);
        }
        return R.success(dtoList);
    }
}













