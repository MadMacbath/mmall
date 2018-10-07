package com.macbeth.to.cart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class CartAddProduct {
    @ApiModelProperty(value = "添加商品数量",name = "count")
    @NotNull
    private Integer count;

    @ApiModelProperty(value = "添加商品ID",name = "productId")
    @NotNull
    private Integer productId;
}

