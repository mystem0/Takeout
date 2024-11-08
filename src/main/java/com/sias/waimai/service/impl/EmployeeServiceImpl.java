package com.sias.waimai.service.impl;

import com.sias.waimai.common.CustomException;
import com.sias.waimai.pojo.Employee;
import com.sias.waimai.mapper.EmployeeMapper;
import com.sias.waimai.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 员工信息 服务实现类
 * </p>
 *
 * @author li
 * @since 2023-05-12
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Override
    public Employee getSomeInfo(Long id) {
        if (id == null){
            throw new CustomException("登录时未获取到id");
        }
        return baseMapper.getSomeInfo(id);
    }
}
