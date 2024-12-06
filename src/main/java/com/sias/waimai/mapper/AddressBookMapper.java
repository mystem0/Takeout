package com.sias.waimai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sias.waimai.pojo.AddressBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author li+
 * @date 2024/11/24 15:58
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {

    @Update("update reggie.address_book set is_default = 0 where user_id = #{uid}")
    Boolean updateIsNotDefault(Long uid);

    @Update("update reggie.address_book set is_default = 1 where id = #{id}")
    Boolean updateIsDefault(Long id);
}
