package com.yuhao.waimai.controller;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuhao.waimai.bean.Category;
import com.yuhao.waimai.bean.Employee;
import com.yuhao.waimai.common.R;
import com.yuhao.waimai.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/*菜品种类控制层*/
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @PostMapping
    public R<String> addDish(@RequestBody Category category){
        log.info("插入{}成功",category);
        categoryService.save(category);
        return R.success("添加成功!");
    }


    //分页查询
    @GetMapping("/page")
    public R<Page> pageQuery(int page, int pageSize){
        //创建分页查询Page对象 分页构造器对象
        Page pageInfo = new Page(page,pageSize);
        //条件过滤器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件 根据sort排序
        lambdaQueryWrapper.orderByDesc(Category::getSort);
        categoryService.page(pageInfo,lambdaQueryWrapper);
        return R.success(pageInfo);
    }
    //删除      如果套餐关联了菜品 那么就不能删除
    // **如果是restful风格传值的话，就要用@PathVariable注解    或@RequestBody注解
    @DeleteMapping
    public R<String> delete(Long ids){
        categoryService.remove(ids);
        /*categoryService.removeById(ids);*/
        return R.success("删除成功");
    }

    /** 分类category 的修改功能实现*/
    @PutMapping
    public R<String> update(@RequestBody Category category){

        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /*请求网址: http://localhost:8083/category/list?type=1
     请求方法: GET*/
    @GetMapping("/list")
    public R<List<Category>> categoryList(Category category){

        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //查询类型为传过来的数值的字段
        lambdaQueryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        /*lambdaQueryWrapper.orderByDesc(Category::getSort).orderByAsc(Category::getUpdateTime);*/
        List<Category> list = categoryService.list(lambdaQueryWrapper);
        return R.success(list);
    }
}
