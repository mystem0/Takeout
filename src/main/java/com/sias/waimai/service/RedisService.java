package com.sias.waimai.service;

import java.util.Map;

/**
 * @author li+
 * @date 2024/11/24 20:01
 */
public interface RedisService {
    void setMailCode(Map<String,String> map);

    String getMailCode(String email);

    void delMailCode(String email);
}
