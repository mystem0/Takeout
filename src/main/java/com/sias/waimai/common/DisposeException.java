package com.sias.waimai.common;

import com.sias.waimai.pojo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;


/**
 * 全局统一异常处理
 */
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
//RestControllerAdvice = ControllerAdvice + ResponseBody
@Slf4j
public class DisposeException {

    /**
     * 添加员工时错误
     *
     * @param a
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> addException(SQLIntegrityConstraintViolationException a) {
        //后端控制台输出错误日志信息
        log.error(a.getMessage());
        if (a.getMessage().contains("Duplicate entry")) {
            String[] split = a.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("添加失败,未知错误");
    }

    /**
     * 异常处理方法
     *
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
