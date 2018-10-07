package com.macbeth.vo.manager.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ProductListVo {

    private Integer id;

    private Integer categoryId;

    private String name;

    private String subtitle;

    private String mainImage;

    private BigDecimal price;

    private Integer status;

    // 图片url前缀
    private String imageHost;
}
