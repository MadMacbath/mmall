package com.macbeth.to.manager.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel
@Data
public class ProductUpdateStatus {
    @ApiModelProperty(value = "产品状态",name = "status")
    @NotNull
    private Integer status;
}
