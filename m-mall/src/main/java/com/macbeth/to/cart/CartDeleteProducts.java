package com.macbeth.to.cart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class CartDeleteProducts {

    @ApiModelProperty(value = "需要删除的商品的id,多个id用逗号隔开",name = "productId")
    @NotNull
    private String productIds;
}

