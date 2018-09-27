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
public class UserRegister {
    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(value = "用户名",name = "username")
    @NotNull
    private String username;

    @ApiModelProperty(value = "用户密码",name = "password")
    @NotNull
    private String password;

    @ApiModelProperty(value = "用户邮箱地址",name = "email")
    @NotNull
    @Email(message = "邮箱地址不合法")
    private String email;

    @ApiModelProperty(value = "用户手机号",name = "phone")
    @NotNull
    private String phone;

    @ApiModelProperty(value = "用户密保问题",name = "question")
    @NotNull
    private String question;

    @ApiModelProperty(value = "用户密保答案",name = "answer")
    @NotNull
    private String answer;

    @ApiModelProperty(hidden = true)
    private Integer role;

    @ApiModelProperty(hidden = true)
    private Date createTime;

    @ApiModelProperty(hidden = true)
    private Date updateTime;
}
