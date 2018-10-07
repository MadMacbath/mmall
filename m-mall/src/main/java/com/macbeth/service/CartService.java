package com.macbeth.service;

import com.macbeth.common.ServerResponse;

public interface CartService {
    ServerResponse add(Integer userId, Integer count, Integer productId);

    ServerResponse update(Integer userId, Integer count, Integer productId);

    ServerResponse delete(Integer userId, String productIds);

    ServerResponse list(Integer useId);

    ServerResponse selectProduct(Integer userId,Integer productId);

    ServerResponse unCheckedProduct(Integer userId,Integer productId);

    ServerResponse getCartProductCount(Integer userId);
}
