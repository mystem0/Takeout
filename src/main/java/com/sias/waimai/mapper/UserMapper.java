package com.sias.waimai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sias.waimai.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author li+
 * @date 2024/11/22 20:49
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select id,name,phone,sex,status from reggie.user where id = #{id}")
    User selectInfo(Long id);
}
