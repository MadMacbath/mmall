package com.macbeth.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @ApiModelProperty(value = "用户ID",name = "id")
    private Integer id;

    @ApiModelProperty(value = "用户名",name = "username")
    private String username;

    @ApiModelProperty(value = "用户密码",name = "password")
    private String password;

    @ApiModelProperty(value = "邮箱",name = "email")
    private String email;

    @ApiModelProperty(value = "电话",name = "phone")
    private String phone;

    @ApiModelProperty(value = "密保问题",name = "question")
    private String question;

    @ApiModelProperty(value = "密保答案",name = "answer")
    private String answer;

    @ApiModelProperty(value = "用户角色",name = "role")
    private Integer role;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间",name = "createTime")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-d HH:mm:ss")
    @ApiModelProperty(value = "更新时间",name = "updateTime")
    private Date updateTime;
}