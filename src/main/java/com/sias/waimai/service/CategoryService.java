package com.sias.waimai.service;

import com.sias.waimai.pojo.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 菜品及套餐分类 服务类
 * </p>
 *
 * @author li
 * @since 2023-05-19
 */
public interface CategoryService extends IService<Category> {
    //根据ID删除分类
    public void remove(Long id);
}
