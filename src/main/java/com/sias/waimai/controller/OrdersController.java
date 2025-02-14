package com.sias.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sias.waimai.common.BaseContext;
import com.sias.waimai.dto.OrdersDto;
import com.sias.waimai.dto.OrdersVO;
import com.sias.waimai.pojo.*;
import com.sias.waimai.service.OrderDetailService;
import com.sias.waimai.service.OrdersService;
import com.sias.waimai.service.ShoppingCartService;
import com.sias.waimai.service.UserService;
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
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;

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

    /**
     * 再来一单
     * @param orders
     * @return
     */
    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        List<OrderDetail> details = orderDetailService.getByOrderId(orders.getId());
        shoppingCartService.saveAgain(details);
        return R.success("再来一单成功");
    }

    /**
     * 后台分页查询
     * @param page
     * @param pageSize
     * @param ov
     * @return
     */
    @GetMapping("/page")
    public R<Page> list(int page, int pageSize, OrdersVO ov){
        log.info("page={},pageSize={},ov={}",page,pageSize,ov);
        Page<Orders> page1 = new Page<>(page,pageSize);
        Page<OrdersVO> voPage = new Page<>();
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Orders::getCheckoutTime);
        wrapper.between(ov.getBeginTime()!=null&&ov.getEndTime()!=null,Orders::getOrderTime,ov.getBeginTime(),ov.getEndTime());//指定时间段
        wrapper.like(ov.getNumber()!=null,Orders::getNumber,ov.getNumber());
        ordersService.page(page1, wrapper);
        //对象拷贝
        BeanUtils.copyProperties(page1,voPage,"records");
        List<Orders> records = page1.getRecords();
        List<OrdersVO> list = records.stream().map((item) -> {
            OrdersVO ordersVO = new OrdersVO();
            BeanUtils.copyProperties(item,ordersVO);
            String idToName = userService.getIdToName(item.getUserId());
            ordersVO.setUserIdName(idToName);
            return ordersVO;
        }).collect(Collectors.toList());
        voPage.setRecords(list);
        return R.success(voPage);
    }

    /**
     * 后台修改订单状态
     * @param orders
     * @return
     */
    @PutMapping()
    public R<String> updateStatus(@RequestBody Orders orders){
        if (ordersService.updateById(orders)){
            return R.success("修改成功");
        }
        return R.error("修改失败");
    }
}
