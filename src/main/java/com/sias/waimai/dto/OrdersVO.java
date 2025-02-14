package com.sias.waimai.dto;

import com.sias.waimai.pojo.Orders;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author li+
 * @date 2025/2/14 17:55
 */
@Data
public class OrdersVO extends Orders {
    //页面展示用户名
    private String userIdName;
    //开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;
    //结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
