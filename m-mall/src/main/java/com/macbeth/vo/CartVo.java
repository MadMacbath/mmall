package com.macbeth.vo;

import com.macbeth.vo.manager.product.CartProductVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVo {
    private List<CartProductVo> cartProductVos;
    private BigDecimal cartTotalPrice;
    private Boolean allChecked;
    private String imageHost;
}
