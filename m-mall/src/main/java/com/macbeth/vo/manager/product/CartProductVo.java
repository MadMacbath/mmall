package com.macbeth.vo.manager.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartProductVo {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private String productName;
    private String productSubtitle;
    private Integer productStatus;
    private String productMainImages;
    private BigDecimal productPrice;
    private Integer status;
    private BigDecimal productTotalPrice;
    private Integer productStock;
    private Integer productChecked;
    private String limitQuantity;
}
