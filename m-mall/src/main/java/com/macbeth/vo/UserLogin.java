package com.macbeth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ToString
@ApiModel
public class UserLogin {
    @ApiModelProperty(value = "用户密码",name = "password")
    @NotNull
    private String password;

}
