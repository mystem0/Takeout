package com.sias.waimai.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author li+
 * @date 2024/11/22 17:49
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String phone;
    private String sex;
    private String idNumber;
    private String avatar;
    private Integer status;
}
