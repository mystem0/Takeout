package com.sias.waimai.dto;

import com.sias.waimai.pojo.Dish;
import com.sias.waimai.pojo.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;    //菜品分类名称    菜品管理分页查询时拓展的属性，封装菜品分类名称

    private Integer copies;
}
