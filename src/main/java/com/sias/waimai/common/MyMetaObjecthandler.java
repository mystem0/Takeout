package com.sias.waimai.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sias.waimai.common.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 *元数据对象处理器
 * 1.先在实体类中需要自动填充的地方添加注解@TableField
 * 2.编写这个处理器
 * 3.删除原先手动添加的高耦合代码
 */
@Slf4j
@Component
public class MyMetaObjecthandler implements MetaObjectHandler {
    /**
     * 插入时自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("插入时自动填充");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
//        metaObject.setValue("createUser", 1L);
//        metaObject.setValue("updateUser", 1L);
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * 更新时自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("更新时自动填充");
        log.info(metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
//        metaObject.setValue("updateUser", 1L);
        metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
