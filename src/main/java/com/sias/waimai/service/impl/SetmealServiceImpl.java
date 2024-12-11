package com.sias.waimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sias.waimai.common.CustomException;
import com.sias.waimai.dto.SetmealDto;
import com.sias.waimai.mapper.DishMapper;
import com.sias.waimai.pojo.Setmeal;
import com.sias.waimai.mapper.SetmealMapper;
import com.sias.waimai.pojo.SetmealDish;
import com.sias.waimai.service.SetmealDishService;
import com.sias.waimai.service.SetmealService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 套餐 服务实现类
 * </p>
 *
 * @author li
 * @since 2023-05-19
 */
@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Value("${waimai.path}")
    private String basePath;

    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private DishMapper dishMapper;
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //select count(*) from setmeal where id in (1,2,3) and status = 1
        //查询套餐状态，确定是否可用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);
        if(count > 0){
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        List<Setmeal> list = this.listByIds(ids);
        List<String> images = list.stream().map(item->{
            String image = basePath + item.getImage();
            return image;
        }).collect(Collectors.toList());
        // 删除图片
        deleteImages(images);

        //如果可以删除，先删除套餐表中的数据---setmeal
        this.removeByIds(ids);

        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        //删除关系表中的数据----setmeal_dish
        setmealDishService.remove(lambdaQueryWrapper);
    }

    /**
     * 删除图片
     * @param images
     */
    public void deleteImages(List<String> images) {
        for (String image : images){
            File file = new File(image);
            if (file.exists()){
                if (file.delete()){
                    log.info("Delete image successfully: {}", image);
                }else {
                    log.error("Failed to delete image: {}", image);
                }
            } else {
                // 图片不存在，记录日志
                log.warn("Image does not exist: {}", image);
            }
        }
    }

    /**
     * 修改套餐
     * @param setmealDto
     */
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);//更新套餐基本信息
        //通过套餐id删除当前套餐的菜品信息
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(wrapper);
        //添加新的菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            if (dishMapper.selectId(item.getDishId()) ==null){
                throw new CustomException("菜品不存在");
            }
            item.setSetmealId(setmealDto.getId());//给每一条菜品数据绑定套餐id
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }
}
