package com.restkeeper.response.exception;

import lombok.Data;

/**
 * @author dinglei
 * @date 2022/5/2 11:02
 */
@Data
public class ExceptionResponse {
    private String msg ;
    public ExceptionResponse(String msg){
        this.msg = msg ;
    }
}
