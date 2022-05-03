package com.restkeeper.operator.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dinglei
 * @date 2022/5/2 11:42
 */
@Data
public class LoginVO {
    @ApiModelProperty(value = "登录账号")
    private String loginName ;

    @ApiModelProperty(value = "登录密码")
    private String loginPass ;
}
