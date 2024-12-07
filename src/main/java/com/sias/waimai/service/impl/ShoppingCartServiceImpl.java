package com.sias.waimai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sias.waimai.mapper.ShoppingCartMapper;
import com.sias.waimai.pojo.ShoppingCart;
import com.sias.waimai.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author li+
 * @date 2024/12/7 14:02
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService{
}
