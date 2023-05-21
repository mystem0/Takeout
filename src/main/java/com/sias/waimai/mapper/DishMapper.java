package com.sias.waimai.mapper;

import com.sias.waimai.pojo.Dish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

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

}
