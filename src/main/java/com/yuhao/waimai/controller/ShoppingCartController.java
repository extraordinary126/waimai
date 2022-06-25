package com.yuhao.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yuhao.waimai.bean.ShoppingCart;
import com.yuhao.waimai.common.BaseContext;
import com.yuhao.waimai.common.R;
import com.yuhao.waimai.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Resource
    private ShoppingCartService shoppingCartService;

   /* 请求网址: http://localhost:8083/shoppingCart/list
      请求方法: GET*/
    @GetMapping("/list")
    public R<List<ShoppingCart>> getList(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        lambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    /* 请求网址: http://localhost:8083/shoppingCart/add
    请求方法: POST*/
    @PostMapping("/add")
    public R<ShoppingCart> addDish(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据{}",shoppingCart);

        //设置用户id,指定当前是哪个用户的购物车数据
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);

        if (dishId != null){
            //有dishId 说明 添加的是菜品
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //添加的是套餐
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //select * from shopping_cart where user_id = ? and (dish_Id) / (setmeal_id) = ?;
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(lambdaQueryWrapper);

        //查询当前菜品或套餐是否在购物车中
        if (shoppingCartOne != null){
            //如果已经存在, 就在原来的基础上加一
            Integer number = shoppingCartOne.getNumber();
            shoppingCartOne.setNumber(number + 1);
            shoppingCartService.updateById(shoppingCartOne);
        }else {
            //如果不存在,则添加到购物车,默认数量就是1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCartOne = shoppingCart;
        }

        return R.success(shoppingCartOne);
    }

    //清空购物车
    /*请求网址: http://localhost:8083/shoppingCart/clean
    请求方法: DELETE*/
    @DeleteMapping("/clean")
    public R<String> cleanShoppingCart(){
        shoppingCartService.clean();
        return R.success("清空购物车成功!");
    }

    /*请求网址: http://localhost:8083/shoppingCart/sub
      请求方法: POST*/
    @PostMapping("/sub")
    public R<ShoppingCart> subInShoppingCart(@RequestBody ShoppingCart shoppingCart){
        //设置用户id,指定当前是哪个用户的购物车数据
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        boolean isDish = false;
        // 当前数量为1时 删除套餐或菜品的 LambdaUpdateWrapper
        LambdaUpdateWrapper<ShoppingCart> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();

        if (shoppingCart.getDishId() != null){
            //说明要减少的是Dish
            //打个标记 减少的是dish
            isDish = true;
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //查询出了当前用户id下购物车 要减少的菜品或套餐信息
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(lambdaQueryWrapper);
        if (shoppingCartOne.getNumber() > 1){
            shoppingCartOne.setNumber(shoppingCartOne.getNumber() - 1);
            shoppingCartService.updateById(shoppingCartOne);
        }else {
            // 先赋0 用于返回前端  反正记录最后都会删除
            shoppingCartOne.setNumber(0);
            //当前数量为 1  判断是菜品还是套餐  直接删除
            if (isDish){
            lambdaUpdateWrapper.eq(ShoppingCart::getDishId,shoppingCartOne.getDishId());
            shoppingCartService.remove(lambdaUpdateWrapper);
            }else {
                lambdaUpdateWrapper.eq(ShoppingCart::getSetmealId,shoppingCartOne.getSetmealId());
                shoppingCartService.remove(lambdaUpdateWrapper);
            }
        }
        return R.success(shoppingCartOne);
    }





  /*  @GetMapping("/list")
    public R<String> getList(HttpSession session){
        String json = "'code':1,'msg':null'data':[],'map':{}";
        return R.success(null);
    }*/
}
