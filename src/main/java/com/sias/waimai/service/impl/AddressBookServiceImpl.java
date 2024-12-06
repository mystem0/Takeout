package com.sias.waimai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sias.waimai.mapper.AddressBookMapper;
import com.sias.waimai.pojo.AddressBook;
import com.sias.waimai.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
