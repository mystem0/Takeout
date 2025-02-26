package com.sias.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sias.waimai.pojo.OrderDetail;

import java.util.List;

/**
 * @author li+
 * @date 2024/12/7 18:49
 */
public interface OrderDetailService extends IService<OrderDetail> {
    List<OrderDetail> getByOrderId(Long id);
}
