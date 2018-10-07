package com.macbeth.service;

import com.github.pagehelper.PageInfo;
import com.macbeth.common.ServerResponse;
import com.macbeth.pojo.Product;
import com.macbeth.vo.manager.product.ProductDetailVo;

public interface ProductService {
    ServerResponse<String> saveOrUpdateProduct(Product product);

    ServerResponse<String> updateStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVo> managerGetProductInformation(Integer productId);

    ServerResponse<PageInfo> listProducts(int pageNum, int pageSize);

    ServerResponse<PageInfo> searchProducts(String name, Integer productId, Integer pageNum, Integer pageSize);

    ServerResponse<ProductDetailVo> getProductById(Integer productId);

    ServerResponse<PageInfo> getProductByKeyWordCategory(String keyWords, Integer categoryId, int pageNum, int pageSize, String orderBy);
}
