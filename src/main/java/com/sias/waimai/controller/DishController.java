package com.sias.waimai.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sias.waimai.dto.DishDto;
import com.sias.waimai.pojo.Category;
import com.sias.waimai.pojo.Dish;
import com.sias.waimai.pojo.DishFlavor;
import com.sias.waimai.pojo.R;
import com.sias.waimai.service.CategoryService;
import com.sias.waimai.service.DishFlavorService;
import com.sias.waimai.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜品及菜品口味 前端控制器
 * </p>
 *
 * @author li
 * @since 2023-05-19
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("获取到新增的菜品信息{}", dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品管理分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器
        Page<Dish> page1 = new Page<>(page, pageSize);
        Page<DishDto> page2 = new Page<>();
        //构建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getId);
        //执行分页查询
        dishService.page(page1, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(page1,page2,"records");
        List<Dish> records = page1.getRecords();
        List<DishDto> list = records.stream().map((item) -> {

            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            //由于原始数据缺陷，有些菜品并没有分类名称
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        page2.setRecords(list);
        return R.success(page2);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //菜品基本信息列表
        List<Dish> list = dishService.list(queryWrapper);
        //菜品基本信息和口味信息
        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //拷贝属性
            BeanUtils.copyProperties(item,dishDto);
            //获取分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                //获取分类名称
                String categoryName = category.getName();
                //赋值给dishDto
                dishDto.setCategoryName(categoryName);
            }
            //当前菜品id
            Long dishId = item.getId();
            //通过当前菜品id查询对应的口味数据
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper= new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            //赋值给dishDto
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }

    /**
     * 批量起售
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public R<String> startStatus(@RequestParam List<Long> ids) {
        log.info("要启售的菜品id为{}",ids);
        //将id从ids中取出来
        for (Long id : ids) {
            //起售无需判断相关套餐状态
            Dish dish = dishService.getById(id);
            dish.setStatus(1);
            dishService.updateById(dish);
        }
        return R.success("启售成功");
    }

    /**
     * 批量停售
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public R<String> stopStatus(@RequestParam List<Long> ids) {
        log.info("要停售的菜品id为{}",ids);
        //将id从ids中取出来
        for (Long id : ids) {
            //判断这个菜品绑定的套餐是否已停售
            if (!dishService.selectSetmealStatus(id)){
                return R.error("菜品正在被起售的套餐使用，无法停售");
            }
            Dish dish = dishService.getById(id);
            dish.setStatus(0);
            dishService.updateById(dish);
        }
        return R.success("停售成功");
    }
}

