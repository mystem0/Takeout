package com.sias.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sias.waimai.pojo.Category;
import com.sias.waimai.pojo.R;
import com.sias.waimai.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 *
 * @author li
 * @since 2023-05-12
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类(菜品+套餐)
     *
     * @param category
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody Category category) {
        log.info("新增分类：{}", category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分类管理 分页查询
     *
     * @param page
     * @param pageSize 不能使用pagesize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        log.info("page={},pageSize={}", page, pageSize);
        //创建分页构造器
        Page<Category> page1 = new Page<>(page, pageSize);
        //因为要根据sort排序，所以要实例化一个条件构造器
        //条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        //添加排序条件，根据sort进行升序排序
        wrapper.orderByAsc(Category::getSort);
        //分页查询  .page(分页构造器，条件构造器)
        categoryService.page(page1, wrapper);
        return R.success(page1);
    }

    /**
     * 删除单个
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delById(Long id) {
        log.info("删除菜品：{}", id);
        //categoryService.removeById(id);
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }

    /**
     * 修改分类信息
     *
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("修改{}", category);
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    /**
     * 菜品管理--->菜品分类
     *
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        //条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        //添加查询条件,布尔类型的condition是eq方法是否执行的前提
        //Category::getType确定查询哪一列，category.getType()确定值，相当于select * from Category where type=category.getType()
        wrapper.eq(category.getType() != null, Category::getType, category.getType());
        //排序条件
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        //列表
        List<Category> list = categoryService.list(wrapper);
        //返回
        return R.success(list);
    }
}
