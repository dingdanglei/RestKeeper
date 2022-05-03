package com.restkeeper.operator.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dinglei
 * @date 2022/5/2 20:54
 */
@Data
@EqualsAndHashCode
public class UpdateEnterpriseAccountVO extends AddEnterpriseAccountVO{
    @ApiModelProperty(value = "企业id")
    private String enterpriseId ;
}
