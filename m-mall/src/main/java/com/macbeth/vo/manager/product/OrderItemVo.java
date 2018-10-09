package com.macbeth.vo.manager.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.macbeth.pojo.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderItemVo {

    private Long orderNo;

    private Integer productId;

    private String productName;

    private String productImage;

    private BigDecimal currentUnitPrice;

    private Integer quantity;

    private BigDecimal totalPrice;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public OrderItemVo(){}

    public OrderItemVo(OrderItem item){
        this.setCreateTime(item.getCreateTime());
        this.setCurrentUnitPrice(item.getCurrentUnitPrice());
        this.setOrderNo(item.getOrderNo());
        this.setProductId(item.getProductId());
        this.setProductImage(item.getProductImage());
        this.setProductName(item.getProductName());
        this.setQuantity(item.getQuantity());
        this.setTotalPrice(item.getTotalPrice());
    }

}
