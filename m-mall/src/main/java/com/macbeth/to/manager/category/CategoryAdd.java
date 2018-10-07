package com.macbeth.to.manager.category;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ApiModel
public class CategoryAdd {

    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(value = "上级类别ID",name = "parentId")
    private Integer parentId = 0;

    @ApiModelProperty(value = "分类名称",name = "name")
    @NotNull
    private String name;

    @ApiModelProperty(hidden = true)
    private Boolean status = true;

    @ApiModelProperty(hidden = true,value = "排序编号",name = "sortOrder")
    private Integer sortOrder;

    @ApiModelProperty(hidden = true)
    private Date createTime;

    @ApiModelProperty(hidden = true)
    private Date updateTime;
}