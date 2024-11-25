package com.sias.waimai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sias.waimai.dto.UserDto;
import com.sias.waimai.pojo.R;
import com.sias.waimai.pojo.User;
import org.springframework.mail.SimpleMailMessage;

/**
 * @author li+
 * @date 2024/11/22 20:47
 */
public interface UserService extends IService<User> {

    // 发送验证码邮件,参数只需要接收收件人即可
    R<String> sendMsg(String email);

    User login(UserDto dto);
}
