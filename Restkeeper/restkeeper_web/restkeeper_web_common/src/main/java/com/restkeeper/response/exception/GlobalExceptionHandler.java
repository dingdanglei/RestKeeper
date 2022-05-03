package com.restkeeper.response.exception;

import com.restkeeper.response.BaseResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author dinglei
 * @date 2022/5/2 11:06
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Object exception(Exception e){
        ExceptionResponse response = new ExceptionResponse(e.getMessage());
        return response ;
    }
}
