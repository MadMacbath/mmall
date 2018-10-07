package com.macbeth.to.manager.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class ProductSearch {

    @ApiModelProperty(value = "产品ID",name = "productId")
    private Integer productId;

    @ApiModelProperty(value = "产品名称",name = "name")
    private String name;

    @ApiModelProperty(value = "当前页",name = "pageNum")
    private int pageNum = 1;

    @ApiModelProperty(value = "页容量",name = "pageSize")
    private int pageSize = 10;
}
