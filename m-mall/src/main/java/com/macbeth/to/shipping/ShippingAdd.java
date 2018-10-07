package com.macbeth.to.shipping;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class ShippingAdd {

    @ApiModelProperty(value = "用户ID",name = "userId")
    @NotNull
    private Integer userId;

    @ApiModelProperty(value = "收货人名称",name = "receiverName")
    @NotNull
    private String receiverName;

    @ApiModelProperty(value = "收货人固定电话",name = "receiverPhone")
    @NotNull
    private String receiverPhone;

    @ApiModelProperty(value = "收货人手机号码",name = "receiverMobile")
    @NotNull
    private String receiverMobile;

    @ApiModelProperty(value = "收货人所在省份",name = "receiverProvince")
    @NotNull
    private String receiverProvince;

    @ApiModelProperty(value = "收货人所在市区",name = "receiverCity")
    @NotNull
    private String receiverCity;

    @ApiModelProperty(value = "收货人所在区县",name = "receiverDistrict")
    @NotNull
    private String receiverDistrict;

    @ApiModelProperty(value = "收货地址",name = "receiverAddresss")
    @NotNull
    private String receiverAddress;

    @ApiModelProperty(value = "收货人邮编",name = "receiverZip")
    @NotNull
    private String receiverZip;
}