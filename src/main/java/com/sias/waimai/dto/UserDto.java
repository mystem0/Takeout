package com.sias.waimai.dto;

import com.sias.waimai.pojo.User;
import lombok.Data;

/**
 * @author li+
 * @date 2024/11/22 17:53
 */
@Data
public class UserDto extends User {
    private String code;
}
