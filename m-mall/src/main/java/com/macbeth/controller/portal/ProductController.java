package com.macbeth.controller.portal;

import com.github.pagehelper.PageInfo;
import com.macbeth.common.ServerResponse;
import com.macbeth.service.ProductService;
import com.macbeth.to.manager.product.ProductInfo;
import com.macbeth.vo.manager.product.ProductDetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "商品接口")
@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    @ApiOperation(value = "查看商品详情")
    @GetMapping(value = "product/{productId}")
    public ServerResponse<ProductDetailVo> detail(@ApiParam(value = "商品ID") @PathVariable("productId") Integer productId){
        return productService.getProductById(productId);
    }

    @ApiOperation(value = "查询商品")
    @GetMapping(value = "products")
    public ServerResponse<PageInfo> searchProducts(ProductInfo productInfo){

        return productService.getProductByKeyWordCategory(productInfo.getKeyWords(),productInfo.getCategoryId(),productInfo.getPageNum(),productInfo.getPageSize(),productInfo.getOrderBy());
    }
}
