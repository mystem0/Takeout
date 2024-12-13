package com.sias.waimai.dto;

import com.sias.waimai.pojo.OrderDetail;
import com.sias.waimai.pojo.Orders;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author li+
 * @date 2024/12/13 11:53
 */
@Data
public class OrdersDto extends Orders {
    // 订单明细
    private List<OrderDetail> orderDetails = new ArrayList<>();
    // 支付状态
    private String pyMethodName;
    // 订单状态
    private String statusName;
}
