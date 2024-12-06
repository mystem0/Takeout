package com.sias.waimai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sias.waimai.common.CustomException;
import com.sias.waimai.dto.UserDto;
import com.sias.waimai.pojo.R;
import com.sias.waimai.service.RedisService;
import com.sun.mail.util.MailSSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.sias.waimai.mapper.UserMapper;
import com.sias.waimai.pojo.User;
import com.sias.waimai.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;

/**
 * @author li+
 * @date 2024/11/22 20:48
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Value("${spring.mail.username}")
    String fromMail;

    @Resource
    JavaMailSender mailSender;
    @Resource
    RedisService redisService;

    /**
     * 发送邮件
     * @param recipient
     * @return
     */
    @Override
    public R<String> sendMsg(String recipient) {
        if (recipient == null){
            throw new CustomException("邮箱为空,获取验证码失败");
        }
        MimeMessage message = mailSender.createMimeMessage();
        try {
            String code = String.valueOf(Math.random()).substring(2, 8);//随机生成一个验证码
//            log.info("验证码为:" + code);
//            MailSSLSocketFactory sslSocketFactory = new MailSSLSocketFactory();
//            sslSocketFactory.setTrustAllHosts(true);
            //邮箱发送内容组成
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject("验证码");//设置邮箱标题
            helper.setText(buildContent(code + ""), true);//设置邮件正文内容
            helper.setTo(recipient);//设置邮件接收人
            helper.setFrom("英才招聘平台" + '<' + fromMail + '>');//设置邮件发件人信息
            mailSender.send(message);//发送邮件
            redisService.setMailCode(code);//将验证码存入redis
            return R.success("邮件已到达");
        }catch (MessagingException e){
            log.error(e.getMessage());
            return R.error("邮件发送失败,请重试!");
        }
    }

    /**
     * 读取邮件模板
     * 替换模板中的信息
     *
     * @param title 内容
     * @return
     */
    public String buildContent(String title) {
        //加载邮件html模板
        ClassPathResource resource = new ClassPathResource("mailtemplate.ftl");
        InputStream inputStream = null;
        BufferedReader fileReader = null;
        StringBuffer buffer = new StringBuffer();
        String line = "";
        try {
            inputStream = resource.getInputStream();
            fileReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = fileReader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            System.out.println("发送邮件读取模板失败{}"+e);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //替换html模板中的参数
        return MessageFormat.format(buffer.toString(), title);
    }

    /**
     * 比对验证码
     * @param dto
     * @return
     */
    @Override
    public User login(UserDto dto) {
        if (redisService.getMailCode(dto.getCode()) == null){
            throw new CustomException("验证码错误");
        }
        redisService.delMailCode(dto.getCode());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone,dto.getPhone());
        return this.baseMapper.selectOne(wrapper);
    }
}
