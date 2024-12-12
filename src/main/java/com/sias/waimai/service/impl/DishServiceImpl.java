package com.sias.waimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sias.waimai.common.CustomException;
import com.sias.waimai.dto.DishDto;
import com.sias.waimai.mapper.SetmealDishMapper;
import com.sias.waimai.pojo.Dish;
import com.sias.waimai.mapper.DishMapper;
import com.sias.waimai.pojo.DishFlavor;
import com.sias.waimai.service.DishFlavorService;
import com.sias.waimai.service.DishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sias.waimai.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜品管理 服务实现类
 * </p>
 *
 * @author li
 * @since 2023-05-19
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Value("${waimai.path}")
    private String basePath;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增菜品，同时保存对应的口味数据
     *
     * @param dishDto
     */
    @Transactional     //控制事务
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long dishId = dishDto.getId();//获取菜品id
        //保存菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();//获取 dishDto 对象中的 flavors 列表
        //使用流式处理，给flavors中的每个口味对象设置菜品id属性
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());//将处理后的列表收集并赋值给 flavors 变量。

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 判断当前菜品是否关联套餐
     * true表示没有关联套餐，可以停售
     * false表示关联套餐，不可以停售
     * @param id
     * @return
     */
    public Boolean selectSetmealStatus(Long id) {
        String setmealId = setmealDishMapper.getSetmealId(id);
        if (setmealId != null) {
            Integer status = setmealService.getById(setmealId).getStatus();
            return status != 1;//true表示可以停售; false表示不可以停售
        }
        return true;//setmealid为空说明没有关联套餐，可以停售
    }

    /**
     * 批量删除菜品，同时删除对应的口味数据
     * @param ids
     */
    @Transactional
    public void removeWithSetmeal(List<Long> ids) {
        for (Long id :ids){
            //判断当前菜品状态是否为1，是则抛出异常
            Integer i = dishMapper.select(id, 1);
            if (i > 0){
                throw new CustomException("菜品正在售卖中，不能删除");
            }
            //判断菜品是否正在被套餐使用
            Integer d = setmealDishMapper.select(id);
            if (d > 0){
                throw new CustomException("菜品正在被套餐使用，不能删除");
            }
        }
        //删除菜品图片
        List<Dish> list = this.listByIds(ids);
        List<String> images = list.stream().map(item->{
            String image = basePath + item.getImage();
            return image;
        }).collect(Collectors.toList());
        setmealService.deleteImages(images);
        //删除菜品表中的数据---dish
        this.removeByIds(ids);
        //删除口味表中的数据---dish_flavor
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(lambdaQueryWrapper);
    }
}
