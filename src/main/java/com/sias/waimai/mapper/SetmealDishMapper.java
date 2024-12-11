package com.sias.waimai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sias.waimai.pojo.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {

    @Select("select setmeal_id from reggie.setmeal_dish where dish_id = #{id}")
    String getSetmealId(Long id);

    @Select("select count(*) from reggie.setmeal_dish where dish_id = #{id}")
    Integer select(@Param("id") Long id);
}
