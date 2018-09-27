package com.macbeth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
@ApiModel
public class UserQuestionAnwser {
    @ApiModelProperty(value = "密保问题",name = "question")
    @NotNull
    private String question;

    @ApiModelProperty(value = "密保答案",name = "answer")
    @NotNull
    private String answer;

}
