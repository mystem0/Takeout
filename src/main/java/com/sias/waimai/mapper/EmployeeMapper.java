package com.sias.waimai.mapper;

import com.sias.waimai.pojo.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 员工信息 Mapper 接口
 * </p>
 *
 * @author li
 * @since 2023-05-12
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    @Select("select name from reggie.employee where id = #{uid}")
    String getName(Integer uid);
}
