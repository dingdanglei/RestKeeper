package com.restkeeper.operator.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dinglei
 * @date 2022/5/2 22:12
 */
@Data
public class ResetPwdVO {
    @ApiModelProperty(value = "企业id")
    private String id ;
    @ApiModelProperty(value = "密码")
    private String password ;
}
