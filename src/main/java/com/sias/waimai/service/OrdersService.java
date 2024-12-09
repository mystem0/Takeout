package com.sias.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sias.waimai.pojo.Orders;

/**
 * @author li+
 * @date 2024/12/7 18:49
 */
public interface OrdersService extends IService<Orders> {
    Boolean submit(Orders orders);
}
