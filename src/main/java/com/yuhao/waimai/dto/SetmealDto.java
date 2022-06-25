package com.yuhao.waimai.dto;


import com.yuhao.waimai.bean.Setmeal;
import com.yuhao.waimai.bean.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
