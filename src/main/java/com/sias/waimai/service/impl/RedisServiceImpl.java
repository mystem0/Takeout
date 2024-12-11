package com.sias.waimai.service.impl;

import com.sias.waimai.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
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
    private RedisTemplate<String, Object> redisTemplate;

    // 设置 Redis 的值，带过期时间（分钟）
    void set(String key, Object value, Integer time) {
        if (key == null || key.isEmpty() || value == null || time <= 0) {
            throw new IllegalArgumentException("Invalid parameters: key, value, or time");
        }
        try {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Error setting value in Redis: " + e.getMessage(), e);
        }
    }

    /**
     * 往 Redis 中存放验证码
     * @param code 验证码集合，key 为邮箱，value 为验证码
     */
    @Override
    public void setMailCode(Map<String, String> code) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Code map cannot be null or empty");
        }
        for (Map.Entry<String, String> entry : code.entrySet()) {
            set(entry.getKey(), entry.getValue(), 5); // 验证码 5分钟过期
        }
        log.info("验证码{}已存入缓存", code);
    }

    /**
     * 从 Redis 中获取验证码
     * @param email 邮箱地址
     * @return 返回验证码，如果没有找到，返回 null
     */
    @Override
    public String getMailCode(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty");
        }
        Object value = get(email);
        return value != null ? (String) value : null; // 如果没有找到验证码，返回 null
    }

    // 获取 Redis 中的值
    Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除验证码
     * @param email 邮箱地址
     */
    @Override
    public void delMailCode(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Phone cannot be null or empty");
        }
        // 判断验证码是否存在
        String verificationCode  = getMailCode(email); // 获取 Redis 中存储的验证码
        if (verificationCode  == null || verificationCode.isEmpty()) {
            log.info("key为{}的验证码不存在或已过期", email);
            return;
        }

        // 如果验证码存在，删除对应的验证码
        del(email);  // 删除对应邮箱的验证码
        log.info("key为{}的验证码 {} 已删除", email,verificationCode);
    }

    // 删除 Redis 中的值
    void del(String key) {
        redisTemplate.delete(key);
    }
}
