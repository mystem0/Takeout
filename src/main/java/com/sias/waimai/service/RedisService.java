package com.sias.waimai.service;

/**
 * @author li+
 * @date 2024/11/24 20:01
 */
public interface RedisService {
    void setMailCode(String code);

    String getMailCode(String code);

    void delMailCode(String code);
}
