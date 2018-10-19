package com.macbeth.service;

import com.github.pagehelper.PageInfo;
import com.macbeth.common.ServerResponse;
import com.macbeth.pojo.Order;
import com.macbeth.to.manager.order.OrderSearch;
import com.macbeth.vo.manager.product.OrderVo;

import java.util.List;
import java.util.Map;

public interface OrderService {

    ServerResponse pay(Integer userId, Long orderNo, String path);

    Order getByOrderNo(String out_trade_no);

    ServerResponse aliCallBack(Map<String, String> param);

    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

    ServerResponse createOrder(Integer userId, Integer shippingId);

    ServerResponse cancell(Integer userId, Long orderNo);

    ServerResponse getSelectedProduct(Integer userId);

    ServerResponse getByUserIdAndOrderNo(Integer userId, Long orderNo);

    ServerResponse list(Integer userId, int pageNum, int pageSize);

    ServerResponse<PageInfo<OrderVo>> managerList(int pageNum, int pageSize);

    ServerResponse<OrderVo> managerDetail(Long orderNo);

    ServerResponse<PageInfo<List<OrderVo>>> managerSearch(OrderSearch orderSearch);

    ServerResponse sendPostage(Long orderNo);

    void closeOrder(Integer hour);
}
