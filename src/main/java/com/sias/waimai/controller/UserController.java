package com.sias.waimai.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sias.waimai.dto.UserDto;
import com.sias.waimai.mapper.UserMapper;
import com.sias.waimai.pojo.R;
import com.sias.waimai.pojo.User;
import com.sias.waimai.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户操作控制器
 * @author li+
 * @date 2024/11/22 17:31
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    /**
     * 发送邮箱验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user){
        String email = user.getPhone();
        return userService.sendMsg(email);
    }

    /**
     * 用户登录
     * @param dto
     * @param request
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody UserDto dto, HttpServletRequest request){
        log.info("用户登录{}", dto.getPhone());
        if (dto.getPhone().isEmpty() || dto.getCode().isEmpty()){
            return R.error("用户名或验证码丢失");
        }
        User user = userService.login(dto);
        //用户已存在
        if (user != null){
            //判断状态是否为0
            if (user.getStatus() == 0){
                return R.error("用户已禁用");
            }
            request.getSession().setAttribute("user",user.getId());
            User user1 = userMapper.selectInfo(user.getId());
            return R.success(user1);
        }
        //用户不存在
        dto.setStatus(1);
        userMapper.insert(dto);//插入数据
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone,dto.getPhone()).eq(User::getStatus,1);
        User one = userMapper.selectOne(wrapper);//再次查询验证
        if (one != null){
            request.getSession().setAttribute("user",one.getId());
            User user2 = userMapper.selectInfo(one.getId());
            return R.success(user2);
        }
        return R.error("登录失败,未成功保存至数据库，请重新注册");
    }
}
