package com.sias.waimai.pojo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author li+
 * @date 2024/12/7 18:47
 */
@Data
public class OrderDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    //主键
    private Long id;
    //名称
    private String name;
    //图片
    private String image;
    //订单号
    private Long orderId;
    //菜品id
    private Long dishId;
    //套餐id
    private Long setmealId;
    //菜品口味
    private String dishFlavor;
    //数量
    private Integer number;
    //金额
    private BigDecimal amount;
}
