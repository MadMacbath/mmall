package com.macbeth.to.cart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class CartUpdateProduct {

    @ApiModelProperty(value = "购物车中商品的数量",name = "count")
    @NotNull
    private Integer count;

    @ApiModelProperty(value = "更新商品ID",name = "productId")
    @NotNull
    private Integer productId;
}

