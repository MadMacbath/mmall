package com.macbeth.to.manager.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel
public class ProductAdd {
    @ApiModelProperty(hidden = true)
    private Integer id;

    @ApiModelProperty(value = "分类ID",name = "categoryId")
    @NotNull
    private Integer categoryId;

    @ApiModelProperty(value = "产品名称",name = "name")
    @NotNull(message = "产品名称不能为空")
    private String name;

    @ApiModelProperty(value = "商品副标题",name = "subtitle")
    @NotNull
    private String subtitle;

    @ApiModelProperty(hidden = true)
    private String mainImage;

    @ApiModelProperty(value = "图片地址",name = "subImages")
    @NotNull
    private String subImages;

    @ApiModelProperty(value = "商品详情",name = "detail")
    @NotNull
    private String detail;

    @ApiModelProperty(value = "商品价格",name = "price")
    @NotNull
    private BigDecimal price;

    @ApiModelProperty(value = "库存数量",name = "stock")
    @NotNull
    private Integer stock;

    @ApiModelProperty(value = "商品状态",name = "status")
    @NotNull
    private Integer status;

}