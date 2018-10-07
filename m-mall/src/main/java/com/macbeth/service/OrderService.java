package com.macbeth.service;

import com.macbeth.common.ServerResponse;
import com.macbeth.pojo.Order;

import java.util.Map;

public interface OrderService {
    ServerResponse pay(Integer userId, Long orderNo, String path);

    Order getByOrderNo(String out_trade_no);

    ServerResponse aliCallBack(Map<String, String> param);

    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);
}
