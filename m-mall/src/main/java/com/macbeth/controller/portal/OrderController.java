package com.macbeth.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.google.common.collect.RangeSet;
import com.macbeth.common.Constant;
import com.macbeth.common.ServerResponse;
import com.macbeth.pojo.Order;
import com.macbeth.pojo.User;
import com.macbeth.service.OrderService;
import com.macbeth.util.ControllerUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.jsqlparser.schema.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Api(tags = "订单接口")
@RestController
public class OrderController {

    private static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;


    @ApiOperation(value = "创建订单")
    @PostMapping(value = "order")
    public ServerResponse create(@ApiIgnore HttpSession session,
                                 @ApiParam(value = "收货地址ID",name = "shippingId") @NotNull Integer shippingId){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        return orderService.createOrder(user.getId(),shippingId);
    }

    @ApiOperation(value = "取消订单")
    @DeleteMapping(value = "order/{orderNo}")
    public ServerResponse cancell(@ApiIgnore HttpSession session,
                                  @PathVariable(value = "orderNo") @ApiParam(value = "订单号",name = "orderNo") Long orderNo){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        return orderService.cancell(user.getId(),orderNo);
    }

    @ApiOperation(value = "获取购物车中已经选中的商品详情")
    @GetMapping(value = "order/products/checked")
    public ServerResponse getInfomation(@ApiIgnore HttpSession session){
        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        return orderService.getSelectedProduct(user.getId());
    }

    @ApiOperation(value = "获取订单详情")
    @GetMapping(value = "order/{orderNo}")
    public ServerResponse details(@ApiIgnore HttpSession session,
                                  @PathVariable(value = "orderNo") @ApiParam(value = "订单好",name = "orderNo") Long orderNo){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        return orderService.getByUserIdAndOrderNo(user.getId(),orderNo);
    }

    @ApiOperation(value = "获取订单列表")
    @GetMapping(value = "orders")
    public ServerResponse list(@ApiIgnore HttpSession session,
                               @ApiParam(value = "当前页",name = "pageNum") @RequestParam(defaultValue = "1") int pageNum,
                               @ApiParam(value = "页容量",name = "pageSize") @RequestParam(defaultValue = "10") int pageSize){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        return orderService.list(user.getId(),pageNum,pageSize);
    }

    @ApiOperation(value = "获取支付二维码")
    @GetMapping(value = "order/pay/{orderNo}")
    public ServerResponse pay(@ApiIgnore HttpSession session,
                              @PathVariable("orderNo") @ApiParam(value = "订单号",name = "orderNo") Long orderNo){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        String path = session.getServletContext().getRealPath("path");

        return orderService.pay(user.getId(),orderNo,path);
    }

    @ApiOperation(value = "支付宝回调方法")
    @GetMapping("order/pay/callback")
    public Object callback(@ApiIgnore HttpServletRequest request){
        Map<String,String[]> map = request.getParameterMap();
        Map<String,String> param = Maps.newHashMap();
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()){
            String name = (String) iterator.next();
            String[] value = map.get(name);
            String valueStr = Arrays.toString(value);
            param.put(name,valueStr);
        }
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",param.get("sign"),param.get("trade_status"),param.toString());

        param.remove("sign_type");
        try {
            boolean rsaCheck = AlipaySignature.rsaCheckV2(param, Configs.getPublicKey(),"utf-8","RSA2");
            if (! rsaCheck)
                return ServerResponse.createByErrorMessage("请求异常");
            Order order = orderService.getByOrderNo(param.get("out_trade_no"));
            if (order == null)
                return ServerResponse.createByErrorMessage("请求异常");
            String totalAmount = param.get("total_amount");
            String sellerId = param.get("seller_id");
            if (! order.getPaymennt().toString().equals(totalAmount) || ! "sellerId".equals(sellerId))
                return ServerResponse.createByErrorMessage("请求异常");

            ServerResponse response = orderService.aliCallBack(param);
            if (response.isError())
                return Constant.AlipayCallBack.RESPONSE_FAILED;

        } catch (AlipayApiException e) {
            return Constant.AlipayCallBack.RESPONSE_FAILED;
        }

        return Constant.AlipayCallBack.RESPONSE_SUCCESS;
    }

    @ApiOperation(value = "查询订单支付状态")
    @GetMapping("order/{orderNo}/status")
    public ServerResponse queryOrderStatus(@ApiIgnore HttpSession session,
                                           @PathVariable("orderNo") @ApiParam(value = "订单号",name = "orderNo") Long orderNo){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        return orderService.queryOrderPayStatus(user.getId(),orderNo);
    }


























}
