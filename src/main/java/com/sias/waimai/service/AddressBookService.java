package com.sias.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sias.waimai.pojo.AddressBook;

import java.util.List;

/**
 * @author li+
 * @date 2024/11/24 15:56
 */
public interface AddressBookService extends IService<AddressBook> {

    Boolean updateIsNotDefault(Long id);

    Boolean updateIsDefault(Long id);

    void removeWithOrder(List<Long> ids);
}
