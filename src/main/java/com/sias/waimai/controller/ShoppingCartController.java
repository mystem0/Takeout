package com.sias.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sias.waimai.common.BaseContext;
import com.sias.waimai.pojo.R;
import com.sias.waimai.pojo.ShoppingCart;
import com.sias.waimai.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * @author li+
 * @date 2024/12/6 20:40
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add (@RequestBody ShoppingCart shoppingCart){
        // 获取用户id
        Long uid = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        //判断该条数据是套餐还是菜品
        if (shoppingCart.getDishId() != null){
            //当前是菜品数据,添加dish_id查询条件
            wrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else if (shoppingCart.getSetmealId() != null){
            //当前是套餐数据,添加setmeal_id查询条件
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //添加公共user_id查询条件
        wrapper.eq(ShoppingCart::getUserId,uid);
        //判断购物车是否存在当前菜品/套餐
        ShoppingCart cart = shoppingCartService.getOne(wrapper);
        if (cart == null) {
            //不存在，添加
            shoppingCart.setUserId(uid);
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            cart = shoppingCart;
        }else {
            //存在，数量+1
            cart.setNumber(cart.getNumber()+1);
            shoppingCartService.updateById(cart);
        }
        return R.success(cart);
    }

    /**
     * 减少购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<String>sub(@RequestBody ShoppingCart shoppingCart){
        //获取uer_id
        Long uid = BaseContext.getCurrentId();
        //根据dishId获取number
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,uid);
        if (shoppingCart.getDishId() != null){
            wrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else if (shoppingCart.getSetmealId() != null){
            wrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(wrapper);
        //判断number是否为1
        if (one.getNumber() == 1){
            shoppingCartService.removeById(one.getId());
        }else {
            one.setNumber(one.getNumber()-1);
            shoppingCartService.updateById(one);
        }
        return R.success("购物车修改成功");
    }

    /**
     * 获取购物车列表
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        //获取用户id
        Long uid = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        //添加条件
        wrapper.eq(ShoppingCart::getUserId,uid);
        wrapper.orderByAsc(ShoppingCart::getCreateTime);
        //查询
        List<ShoppingCart> cartList = shoppingCartService.list(wrapper);
        return R.success(cartList);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String>clean(){
        Long uid = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,uid);
        if (shoppingCartService.remove(wrapper)){
            return R.success("清空购物车成功");
        }
        return R.error("清空购物车失败");
    }
}
