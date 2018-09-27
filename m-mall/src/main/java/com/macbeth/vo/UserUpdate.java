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
public class UserUpdate {
    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(hidden = true)
    private String username;

    @ApiModelProperty(hidden = true)
    private String password;

    @ApiModelProperty(value = "用户邮箱地址",name = "email")
    @Email
    private String email;

    @ApiModelProperty(value = "用户手机号",name = "phone")
    private String phone;

    @ApiModelProperty(value = "用户密保问题",name = "question")
    private String question;

    @ApiModelProperty(value = "用户密保答案",name = "answer")
    private String answer;

    @ApiModelProperty(hidden = true)
    private Integer role;

    @ApiModelProperty(hidden = true)
    private Date createTime;

    @ApiModelProperty(hidden = true)
    private Date updateTime;
}
