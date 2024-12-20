package com.sias.waimai.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author li+
 * @date 2024/11/24 15:51
 */
@Data
public class AddressBook implements Serializable {
    private static final long serialVersionUID = 1L;
    //主键
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    //用户id
    private String userId;
    //收货人
    private String consignee;
    //性别
    private String sex;
    //手机号
    private String phone;
    //省级编号
    private String provinceCode;
    private String provinceName;
    private String cityCode;
    private String cityName;
    private String districtCode;
    private String districtName;
    //详细地址
    private String detail;
    //标签
    private String label;
    //是否为默认
    private Integer isDefault;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
    //是否删除
    private Integer isDeleted;
}
