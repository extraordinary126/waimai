package com.yuhao.waimai.dto;

//DTO  Data Transfer Object  数据传输对象 用于展示层和服务层传输对象
/*{name: "32", price: 232300, code: "", image: "84c43f4b-cc00-491a-812c-d0290a19ee72.jpg",…}
categoryId: "1413341197421846529"
code: ""
description: "222"
flavors: [{name: "甜味", value: "["无糖","少糖","半糖","多糖","全糖"]", showOption: false},…]
image: "84c43f4b-cc00-491a-812c-d0290a19ee72.jpg"
name: "32"
price: 232300
status: 1*/


//因为多了一个flavors 普通的Dish类不能接收 所以写一个DTO来接收前端的数据

import com.yuhao.waimai.bean.Dish;
import com.yuhao.waimai.bean.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data               //继承了Dish类
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
