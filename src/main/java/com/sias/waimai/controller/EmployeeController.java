package com.sias.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sias.waimai.pojo.Employee;
import com.sias.waimai.pojo.R;
import com.sias.waimai.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 员工信息 前端控制器
 *
 * @author li
 * @since 2023-05-12
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录
     * 'url': '/employee/login',
     * 'method': 'post',
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        //使用eq方法设置查询条件
        wrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(wrapper);
        //3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("error");
        }
        //4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("失败");
        }
        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("员工已被开除");
        }
        //6、登录成功，将员工id存入Session
        request.getSession().setAttribute("employee", emp.getId());
        //根据用户id获取localStorage所需信息
        Employee e = employeeService.getSomeInfo(emp.getId());
        //7、返回登录成功结果
        return R.success(e);
    }

    /**
     * 退出
     * 'url': '/employee/logout',
     * 'method': 'post',
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * url: '/employee',
     * method: 'post',
     * data: { ...params }
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工的信息：{}", employee.toString());
        //设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
/*      原逻辑代码块迁徙至MyMetaObjecthandler.java中
        // 设置员工创建时间和更新时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        // 获得当前登录用户的id
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
*/
        employeeService.save(employee);
        R<String> r = R.success("添加成功");
        return r;
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> list(int page, int pageSize, String name) {
        log.info("page={},pageSize={},name={}", page, pageSize, name);
        //构造分页器
        Page page1 = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //排序
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(page1, lambdaQueryWrapper);
        return R.success(page1);
    }

    /**
     * 修改员工状态并更新员工信息
     *
     * @param request
     * @param employee
     * @return R<String>
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());
//        Long employee1 = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(employee1);
        employeeService.updateById(employee);
        return R.success("状态更新成功");
    }

    /**
     * 修改信息前，根据id查员工信息
     * 可直接保存修改，因为上面已经写好了修改状态时已经写好了更新方法，这两个操作共同调用了add.html，此时复用了update方法。<br>
     * &#064;PathVariable  代表路径参数，也就是说id这个变量在整个路径里面传递，所以可直接用@PathVariable接收
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> saveById(@PathVariable Long id) {
        log.info("开始查询员工{}的信息", id);
        Employee byId = employeeService.getById(id);
        if (byId != null) {
            return R.success(byId);
        }else {
            return R.error("未获取到员工id");
        }
    }


}

