package com.yuhao.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuhao.waimai.bean.Category;

public interface CategoryService extends IService<Category> {

    //自定义删除方法 根据id删除分类 在分类关联了菜品或套餐时不删除
    void remove(Long id);
}
