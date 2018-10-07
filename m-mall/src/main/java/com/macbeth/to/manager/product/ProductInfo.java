package com.macbeth.to.manager.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class ProductInfo {

    @ApiModelProperty(value = "产品ID",name = "categoryId")
    private Integer categoryId;

    @ApiModelProperty(value = "关键字",name = "keyWords")
    private String keyWords;

    @ApiModelProperty(value = "排序值",name = "orderBy",allowableValues = "price_asc,price_desc")
    private String orderBy;

    @ApiModelProperty(value = "当前页",name = "pageNum")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页容量",name = "pageSize")
    private Integer pageSize = 10;
}
