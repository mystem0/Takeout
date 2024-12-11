package com.sias.waimai.service;

import com.sias.waimai.dto.SetmealDto;
import com.sias.waimai.pojo.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 套餐 服务类
 * </p>
 *
 * @author li
 * @since 2023-05-19
 */
public interface SetmealService extends IService<Setmeal> {

    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);

    void deleteImages(List<String> images);

    void updateWithDish(SetmealDto setmealDto);
}
