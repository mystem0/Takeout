package com.sias.waimai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@Slf4j
@ServletComponentScan
@EnableTransactionManagement //开启对事务管理的支持
public class WaimaiApplication {
    /*
     * 解决druid 日志报错：discard long time none received connection:xxx
     * */
    static {
        System.setProperty("druid.mysql.usePingMethod","false");
    }
    public static void main(String[] args) {
        SpringApplication.run(WaimaiApplication.class, args);
        log.info("启动成功");
    }

}
