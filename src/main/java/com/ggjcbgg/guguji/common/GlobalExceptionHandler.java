package com.ggjcbgg.guguji.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})//拦截 类上加了RestController注解的类
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 进行异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.info(ex.getMessage());
        //ex.getMessage-->Duplicate entry 'zhangsan' for key 'idx_username'
        if(ex.getMessage().contains("Duplicate entry")){
            //获取用户名，用空格分隔成字符串数组
            String[] split = ex.getMessage().split(" ");
            //第3个，索引为2是用户名
            String msg = split[2]+"已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 处理异常
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        log.info(ex.getMessage());
        //获得异常信息
        return R.error(ex.getMessage());
    }

}
