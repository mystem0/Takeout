package com.sias.waimai.service.impl;

import com.sias.waimai.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * redis 操作
 * @author li+
 * @date 2024/11/24 20:01
 */
@Slf4j
@Service
public class RedisServiceImpl implements RedisService {

    @Resource
    private RedisTemplate redisTemplate;

    void set(String key,Object value,Integer time){
        redisTemplate.opsForValue().set(key,value,time, TimeUnit.MINUTES);
    }

    /**
     * 往redis中存放验证码
     * @param code
     */
    @Override  /* 验证码 5分钟过期 */
    public void setMailCode(String code) {
        set("mailCode_"+code,code,5);
        log.info("验证码{}已存入缓存",code);
    }

    /**
     * 从redis中获取验证码
     * @param code
     * @return
     */
    @Override
    public String getMailCode(String code) {
        //获取值与code相等的验证码
        Object data = get("mailCode_"+code);
        //没有得到验证码
        if (data==null){
            return null;
        }
        //得到验证码
        return String.valueOf(data);
    }

    Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除验证码
     * @param code
     */
    @Override
    public void delMailCode(String code) {
        // 先判断这个验证码是否被删除了
        if (getMailCode(code).isEmpty()){
            return;
        }
        // 如果没有被删除, 那么执行删除操作
        del("mailCode_"+code);
        log.info("验证码{}已删除",code);
    }

    void del(String key){
        redisTemplate.delete(key);
    }

}
