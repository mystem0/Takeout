package com.sias.waimai.mapper;

import com.sias.waimai.pojo.Dish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 菜品管理 Mapper 接口
 * </p>
 *
 * @author li
 * @since 2023-05-19
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

    @Select("select * from reggie.dish where id = #{dishId}")
    Dish selectId(Long dishId);

    @Select("select count(*) from reggie.dish where id = #{id} and status = #{status}")
    Integer select(@Param("id") Long id,@Param("status") Integer status);
}
