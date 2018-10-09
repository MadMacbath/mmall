package com.macbeth.vo.manager.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OrderVo {
    private Long orderNo;

    private BigDecimal paymennt;

    private Integer paymentType;

    private String paymentTypeDesc;

    private Integer postage;

    private Integer status;

    private String statusDesc;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date paymentTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date closeTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    //订单明细
    private List<OrderItemVo> orderItemVos;

    private String imageHost;

    private Integer ShippingId;

    private String receiverName;

    private ShippingVo shippingVo;

}
