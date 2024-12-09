package com.sias.waimai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sias.waimai.mapper.OrderDetailMapper;
import com.sias.waimai.pojo.OrderDetail;
import com.sias.waimai.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author li+
 * @date 2024/12/7 18:51
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
