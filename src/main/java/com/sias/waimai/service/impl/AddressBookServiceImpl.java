package com.sias.waimai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sias.waimai.common.CustomException;
import com.sias.waimai.mapper.AddressBookMapper;
import com.sias.waimai.mapper.OrdersMapper;
import com.sias.waimai.pojo.AddressBook;
import com.sias.waimai.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author li+
 * @date 2024/11/24 15:57
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Override
    public Boolean updateIsNotDefault(Long uid) {
        return addressBookMapper.updateIsNotDefault(uid);
    }

    @Override
    public Boolean updateIsDefault(Long id) {
        return addressBookMapper.updateIsDefault(id);
    }

    public void removeWithOrder(List<Long> ids) {
        for (Long id : ids){
            //默认地址无法删除
            if (addressBookMapper.selectById(id).getIsDefault() == 1){
                throw new CustomException("默认地址无法删除");
            }
//            //有使用此地址的订单无法删除
//            LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
//            wrapper.eq(Orders::getAddressBookId,id);
//            if (!ordersMapper.selectList(wrapper).isEmpty()){
//                throw new CustomException("有订单正在使用此地址,无法删除");
//            }
        }
        //根据id删除地址
        addressBookMapper.deleteBatchIds(ids);
    }
}
