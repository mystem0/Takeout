package com.sias.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sias.waimai.pojo.OrderDetail;
import com.sias.waimai.pojo.ShoppingCart;

import java.util.List;

/**
 * @author li+
 * @date 2024/12/7 12:22
 */
public interface ShoppingCartService extends IService<ShoppingCart> {
    void saveAgain(List<OrderDetail> details);
}
