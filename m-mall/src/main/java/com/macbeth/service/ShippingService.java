package com.macbeth.service;

import com.macbeth.common.ServerResponse;
import com.macbeth.pojo.Shipping;

public interface ShippingService {

    ServerResponse<Integer> add(Shipping shipping);

    ServerResponse delete(Integer userId, Integer shippingId);

    ServerResponse update(Shipping shipping);

    ServerResponse select(Integer userId, Integer shippingId);

    ServerResponse list(Integer userId, Integer pageNum, Integer pageSize);
}
