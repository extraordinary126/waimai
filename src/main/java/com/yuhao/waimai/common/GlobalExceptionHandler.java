package com.yuhao.waimai.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception){
        // [nio-8083-exec-4] com.yuhao.common.GlobalExceptionHandler  : Duplicate entry 'yuhao' for key 'employee.idx_username'
        log.error(exception.getMessage());
            //如果错误信息里有这一条 说明用户名重复
        if (exception.getMessage().contains("Duplicate entry")){
            String[] split = exception.getMessage().split(" ");
            String msg = split[2];
            return R.error("添加失败," + msg + "已存在 ");
        }
        return R.error("未知错误");
    }
    @ExceptionHandler(CustomException.class)
    public R<String> CustomExceptionHandler(CustomException exception){
        log.error(exception.getMessage());

        return R.error(exception.getMessage());
    }
}
