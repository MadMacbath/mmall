package com.macbeth.to;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
@ApiModel
public class UserValidator {
    @ApiModelProperty(value = "检验字段值",name = "str")
    @NotNull
    private String str;

    @ApiModelProperty(value = "校验类型 USERNAME、EMAIL",name = "type")
    @NotNull
    private String type;

}
