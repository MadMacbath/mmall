package com.macbeth.to;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
@ApiModel
public class UserLogin {
    @ApiModelProperty(value = "用户密码",name = "password")
    @NotNull
    private String password;

}
