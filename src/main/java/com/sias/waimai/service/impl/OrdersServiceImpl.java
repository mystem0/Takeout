package com.sias.waimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sias.waimai.common.BaseContext;
import com.sias.waimai.common.CustomException;
import com.sias.waimai.mapper.OrdersMapper;
import com.sias.waimai.pojo.*;
import com.sias.waimai.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author li+
 * @date 2024/12/7 18:50
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户支付
     * @param orders
     * @return
     */
    @Transactional
    public Boolean submit(Orders orders) {
        // 获取用户id
        Long uid = BaseContext.getCurrentId();
        // 查询该用户购物车列表信息
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,uid);
        List<ShoppingCart> cart = shoppingCartService.list(wrapper);
        // 对购物车信息进行判空
        if (cart == null || cart.isEmpty()){
            throw new CustomException("购物车为空，不能下单");
        }
        //查询用户数据
        User user = userService.getById(uid);
        //查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        // 对地址信息进行判空
        if (addressBook == null){
            throw new CustomException("用户地址信息为空，不能下单");
        }
        Long order_id = IdWorker.getId();//构建订单id
        //计算订单金额并设置订单明细表数据
        AtomicInteger atomicInteger = new AtomicInteger(0);
        //使用stream流遍历购物车数据
        List<OrderDetail> list = cart.stream().map(item -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setName(item.getName());//设置菜品名称
            orderDetail.setImage(item.getImage());//设置菜品图片
            orderDetail.setOrderId(order_id);//设置订单id
            orderDetail.setDishId(item.getDishId());//设置菜品id
            orderDetail.setSetmealId(item.getSetmealId());//设置套餐id
            orderDetail.setDishFlavor(item.getDishFlavor());//设置菜品口味
            orderDetail.setNumber(item.getNumber());//设置份数
            orderDetail.setAmount(item.getAmount());//设置一份的金额
            //计算总金额（单份金额*份数）
            atomicInteger.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        //依次设置order对象数据
        orders.setId(order_id);// 设置订单id
        orders.setNumber(UUID.randomUUID().toString());// 通过UUID设置唯一订单号
        orders.setStatus(2);// 设置订单状态
        orders.setUserId(uid);// 设置用户id
        orders.setOrderTime(LocalDateTime.now());// 设置下单时间
        orders.setCheckoutTime(LocalDateTime.now());// 设置结账时间
        orders.setAmount(new BigDecimal(atomicInteger.get()));// 设置订单总金额
        orders.setPhone(user.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null?"" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null?"" : addressBook.getCityName())
                +(addressBook.getDistrictName() ==null?"" : addressBook.getDistrictName())
                +(addressBook.getDetail() == null?"" : addressBook.getDetail())
        );//设置完整地址
        orders.setUserName(user.getName());// 设置下单人名字
        orders.setConsignee(addressBook.getConsignee());// 设置收货人名字
        this.save(orders);//向order表插入订单数据
        //向order_detail表插入订单明细数据
        orderDetailService.saveBatch(list);
        //清空购物车
        shoppingCartService.remove(wrapper);
        return true;
    }
}
