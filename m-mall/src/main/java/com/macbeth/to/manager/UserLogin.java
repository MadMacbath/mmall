package com.macbeth.to.manager;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class UserLogin {
    @ApiModelProperty(value = "用户名",name = "username")
    @NotNull
    private String username;

    @ApiModelProperty(value = "用户密码",name = "password")
    @NotNull
    private String password;
}
