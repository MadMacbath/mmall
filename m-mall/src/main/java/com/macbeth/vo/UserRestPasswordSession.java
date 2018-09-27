package com.macbeth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
@ApiModel
public class UserRestPasswordSession {
    @ApiModelProperty(value = "用户密码",name = "passwordOld")
    @NotNull
    private String passwordOld;

    @ApiModelProperty(value = "用户新密码",name = "passwordNew")
    @NotNull
    private String passwordNew;

}
