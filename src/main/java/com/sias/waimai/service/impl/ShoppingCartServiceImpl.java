package com.sias.waimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sias.waimai.common.BaseContext;
import com.sias.waimai.mapper.ShoppingCartMapper;
import com.sias.waimai.pojo.OrderDetail;
import com.sias.waimai.pojo.ShoppingCart;
import com.sias.waimai.service.ShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author li+
 * @date 2024/12/7 14:02
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService{

    /**
     * 再来一单，购物车数据回显
     * @param details orderDetail数据
     */
    @Transactional
    public void saveAgain(List<OrderDetail> details) {
        Long uid = BaseContext.getCurrentId();//获取用户id
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,uid);
        this.remove(wrapper);//清空原购物车
        List<ShoppingCart> list = details.stream().map(item -> {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setName(item.getName());
            shoppingCart.setImage(item.getImage());
            shoppingCart.setUserId(uid);
            shoppingCart.setDishId(item.getDishId());
            shoppingCart.setSetmealId(item.getSetmealId());
            shoppingCart.setDishFlavor(item.getDishFlavor());
            shoppingCart.setNumber(item.getNumber());
            shoppingCart.setAmount(item.getAmount());
            shoppingCart.setCreateTime(LocalDateTime.now());
            return shoppingCart;
        }).collect(Collectors.toList());
        this.saveBatch(list);
    }
}
