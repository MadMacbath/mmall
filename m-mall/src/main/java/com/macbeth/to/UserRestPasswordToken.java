package com.macbeth.to;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
@ApiModel
public class UserRestPasswordToken {
    @ApiModelProperty(value = "用户密码",name = "password")
    @NotNull
    private String password;

    @ApiModelProperty(value = "用户令牌",name = "token")
    @NotNull
    private String token;

}
