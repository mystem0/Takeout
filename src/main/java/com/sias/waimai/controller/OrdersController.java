package com.sias.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sias.waimai.common.BaseContext;
import com.sias.waimai.dto.OrdersDto;
import com.sias.waimai.pojo.*;
import com.sias.waimai.service.OrderDetailService;
import com.sias.waimai.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author li+
 * @date 2024/12/7 18:44
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单支付
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据:{}",orders);
        if (ordersService.submit(orders)){
            return R.success("下单成功");
        }
        return R.error("下单失败");
    }

    /**
     * 展示历史订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize) {
        //构造分页构造器
        Page<Orders> page1 = new Page<>(page, pageSize);
        Page<OrdersDto> page2 = new Page<>();
        Long id = BaseContext.getCurrentId();
        //构建条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.eq(Orders::getUserId, id);
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        //执行分页查询
        ordersService.page(page1, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(page1,page2,"records");
        List<Orders> records = page1.getRecords();
        List<OrdersDto> list = records.stream().map((item) -> {

            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);
            //设置detail值
            List<OrderDetail> details = orderDetailService.getByOrderId(item.getId());
            ordersDto.setOrderDetails(details);
            return ordersDto;
        }).collect(Collectors.toList());
        page2.setRecords(list);
        return R.success(page2);
    }
}
