package com.macbeth.to.manager.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class OrderSearch {

    @ApiModelProperty(value = "订单号",name = "orderNo")
    private Long orderNo;

    @ApiModelProperty(value = "当前页",name = "pageNum")
    private int pageNum = 1;

    @ApiModelProperty(value = "页容量",name = "pageSize")
    private int pageSize = 10;
}
