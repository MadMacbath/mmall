package com.macbeth.to.manager.category;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class CategoryUpdateName {
    @ApiModelProperty(value = "类别ID",name = "id")
    @NotNull
    private Integer id;

    @ApiModelProperty(value = "类别名称",name = "name")
    @NotNull
    private String name;
}
