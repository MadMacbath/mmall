package com.macbeth.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.macbeth.common.Constant;
import com.macbeth.common.ServerResponse;
import com.macbeth.dao.*;
import com.macbeth.pojo.*;
import com.macbeth.service.OrderService;
import com.macbeth.to.manager.order.OrderSearch;
import com.macbeth.util.BigDecimalUtils;
import com.macbeth.util.FileUtils;
import com.macbeth.util.PropertiesUtils;
import com.macbeth.vo.manager.product.OrderItemVo;
import com.macbeth.vo.manager.product.OrderProductVo;
import com.macbeth.vo.manager.product.OrderVo;
import com.macbeth.vo.manager.product.ShippingVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.reflections.Reflections.log;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

    private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse pay(Integer userId, Long orderNo, String path) {
        Order order = orderMapper.selectByOrderNoAndUserId(userId,orderNo);
        if (order == null)
            return ServerResponse.createByErrorMessage("用户没有该订单");

        Map<String,String> map = Maps.newHashMap();
        map.put("orderNo",orderNo.toString());

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "当面付扫码消费" + outTradeNo;

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPaymennt().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(order.getOrderNo()).append("购买商品共").append(order.getPaymennt().toString()).toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = null;

        List<OrderItem> orderItems = orderItemMapper.getByOrderNoAndUserId(orderNo,userId);
        goodsDetailList = orderItems.stream().map(orderItem -> GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                BigDecimalUtils.mul(orderItem.getCurrentUnitPrice().doubleValue(),100).longValue(),orderItem.getQuantity()))
                .collect(Collectors.toList());

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl("http://www.test-notify-url.com")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");
                File file = new File(path);
                if (! file.exists()){
                    file.setWritable(true);
                    file.mkdirs();
                }
                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                // 需要修改为运行机器上的路径
                String qrPath = String.format(path + "/qr-%s.png",response.getOutTradeNo());
                String qrName = String.format("qr-%s.png",response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                File targetFile = new File(path,qrName);
                FileUtils.uploadFile(Arrays.asList(targetFile));
                logger.info("filePath:" + qrPath);

                String qrUrl = PropertiesUtils.getProperty("") + targetFile.getName();
                map.put("qrUrl",qrUrl);
                return ServerResponse.createBySuccess(map);

            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常");
        }
    }

    @Override
    public Order getByOrderNo(String out_trade_no) {
        return orderMapper.selectByOrderNo(out_trade_no);
    }

    @Override
    public ServerResponse aliCallBack(Map<String, String> param) {
        Order order = orderMapper.selectByOrderNo(param.get("out_trade_no"));
        if (order.getStatus() >= Constant.OrderStatus.HAD_PAY.getCode())
            return ServerResponse.createBySuccessMessage("支付宝重复调用");
        if (param.get("trade_status").equals(Constant.AlipayCallBack.TRADE_SUCCESS)){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                order.setPaymentTime(format.parse(param.get("gmt_payment")));
            } catch (ParseException e) {
                logger.error("支付时间转换出错");
            }
            order.setStatus(Constant.OrderStatus.HAD_PAY.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        PayInfo payInfo = new PayInfo();
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Constant.PayPlatform.ALIPAY.getCode());
        payInfo.setUserId(order.getUserId());
        payInfo.setPlatformNumber(param.get("trade_no"));
        payInfo.setPlatformStatus(param.get("trade_status"));

        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByOrderNoAndUserId(userId,orderNo);
        if (order == null)
            return ServerResponse.createByErrorMessage("没有该订单");
        if (order.getStatus() >= Constant.OrderStatus.HAD_PAY.getCode())
            return ServerResponse.createBySuccess(true);
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse createOrder(Integer userId, Integer shippingId) {
        List<Cart> carts = cartMapper.querySelectedByUserId(userId);
        ServerResponse response = this.getCartOrderItem(userId,carts);
        if (response.isError())
            return response;
        List<OrderItem> orderItems = (List<OrderItem>) response.getData();
        BigDecimal totalPrice = orderItems.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal::add).get();

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setStatus(Constant.OrderStatus.WAIT_FOR_PAY.getCode());
        order.setPostage(0);
        order.setPaymentType(Constant.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPaymennt(totalPrice);
        order.setUserId(userId);
        order.setShippingId(shippingId);
        int rowCount = orderMapper.insert(order);
        if (rowCount <= 0)
            return ServerResponse.createByErrorMessage("订单创建失败");

        orderItems.stream().forEach(orderItem -> orderItem.setOrderNo(order.getOrderNo()));
        orderItemMapper.insertList(orderItems);

        // 减少库存
        orderItems.stream().forEach(orderItem -> {
            Product product = productMapper.getById(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        });

        // 清空购物车
        List<Integer> list = carts.stream().map(Cart::getId).collect(Collectors.toList());
        cartMapper.deleteByPrimaryKeys(list);

        return ServerResponse.createBySuccess(assembleOrderVo(order,orderItems));
    }

    @Override
    public ServerResponse cancell(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByOrderNoAndUserId(userId,orderNo);
        if (order == null)
            return ServerResponse.createByErrorMessage("订单不存在");
        if (order.getStatus() != Constant.OrderStatus.WAIT_FOR_PAY.getCode())
            return ServerResponse.createByErrorMessage("订单已付款");
        order.setStatus(Constant.OrderStatus.CANCELL.getCode());
        int rowCount = orderMapper.updateByPrimaryKeySelective(order);
        if(rowCount <= 0)
            return ServerResponse.createByErrorMessage("修改订单失败");

        return ServerResponse.createBySuccessMessage("订单取消成功");
    }

    @Override
    public ServerResponse getSelectedProduct(Integer userId) {
        OrderProductVo orderProductVo = new OrderProductVo();
        List<Cart> carts = cartMapper.querySelectedByUserId(userId);
        ServerResponse response = this.getCartOrderItem(userId,carts);
        if (response.isError())
            return response;

        List<OrderItem> orderItems = (List<OrderItem>) response.getData();
        List<OrderItemVo> orderItemVos = orderItems.stream().map(item -> new OrderItemVo(item)).collect(Collectors.toList());
        orderProductVo.setOrderItemVoList(orderItemVos);
        BigDecimal totalPrice = orderItemVos.stream().map(OrderItemVo::getTotalPrice).reduce(BigDecimal::add).get();
        orderProductVo.setProductTotalPrice(totalPrice);
        orderProductVo.setImageHost(PropertiesUtils.getProperty(Constant.IMAGE_HOST));

        return ServerResponse.createBySuccess(orderProductVo);
    }

    @Override
    public ServerResponse getByUserIdAndOrderNo(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByOrderNoAndUserId(userId,orderNo);
        if (order == null)
            return ServerResponse.createByErrorMessage("订单不存在");
        List<OrderItem> orderItems = orderItemMapper.getByOrderNoAndUserId(orderNo,userId);
        OrderVo orderVo = this.assembleOrderVo(order,orderItems);
        return ServerResponse.createBySuccess(orderVo);
    }

    @Override
    public ServerResponse list(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orders = orderMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(orders);

        List<OrderVo> orderVos = orders.stream().map(order -> {
            List<OrderItem> orderItems = orderItemMapper.getByOrderNoAndUserId(order.getOrderNo(),userId);
            return assembleOrderVo(order,orderItems);
        }).collect(Collectors.toList());

        pageInfo.setList(orderVos);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<PageInfo<OrderVo>> managerList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orders = orderMapper.lists();
        List<OrderVo> orderVos = orders.stream().map(order -> {
            List<OrderItem> orderItems = orderItemMapper.getByOrderNo(order.getOrderNo());
            return assembleOrderVo(order,orderItems);
        }).collect(Collectors.toList());

        PageInfo pageInfo = new PageInfo(orders);
        pageInfo.setList(orderVos);

        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<OrderVo> managerDetail(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo.toString());
        if (order == null)
            return ServerResponse.createByErrorMessage("订单不存在");
        List<OrderItem> orderItems = orderItemMapper.getByOrderNo(orderNo);
        OrderVo orderVo = this.assembleOrderVo(order,orderItems);
        return ServerResponse.createBySuccess(orderVo);
    }

    @Override
    public ServerResponse<PageInfo<List<OrderVo>>> managerSearch(OrderSearch orderSearch) {
        PageHelper.startPage(orderSearch.getPageNum(),orderSearch.getPageSize());
        List<Order> orders = orderMapper.managerSearch(orderSearch);
        List<OrderVo> orderVos = orders.stream().map(order -> {
            List<OrderItem> orderItems = orderItemMapper.getByOrderNo(order.getOrderNo());
            return assembleOrderVo(order,orderItems);
        }).collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo(orders);
        pageInfo.setList(orderVos);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse sendPostage(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo.toString());
        if (order == null)
            return ServerResponse.createByErrorMessage("订单不存在");

        if (order.getStatus() == Constant.OrderStatus.HAD_PAY.getCode()){
            order.setStatus(Constant.OrderStatus.SHIPPED.getCode());
            order.setSendTime(new Date());
            int rowCount = orderMapper.updateByPrimaryKeySelective(order);
            if (rowCount > 0)
                return ServerResponse.createBySuccessMessage("发货成功");
        }
        return ServerResponse.createByErrorMessage("发货失败");
    }

    @Override
    public void closeOrder(Integer hour) {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY,-hour);
        List<Order> orders = orderMapper.selectByStateAndCreateTime(Constant.OrderStatus.WAIT_FOR_PAY.getCode(), calendar.getTime());
        orders.stream().forEach(order -> {
            List<OrderItem> orderItems = orderItemMapper.getByOrderNo(order.getOrderNo());
            orderItems.stream().forEach(item -> {
                Integer stock = productMapper.getStockByProductId(item.getProductId());
                if (stock != null) {
                    Product product = new Product();
                    product.setId(item.getProductId());
                    product.setStock(stock + item.getQuantity());
                    productMapper.updateByPrimaryKeySelective(product);
                }
            });
            order.setStatus(Constant.OrderStatus.CANCELL.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        });
    }

    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItems){
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPaymennt(order.getPaymennt());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Constant.PaymentTypeEnum.getByCode(order.getPaymentType()).getValue());

        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Constant.OrderStatus.getByCode(order.getStatus()).getValue());

        orderVo.setShippingId(order.getShippingId());

        Shipping shipping = shippingMapper.getById(order.getShippingId());
        if (shipping != null) {
            orderVo.setReceiverName(shipping.getReceiverName());

            ShippingVo shippingVo = new ShippingVo();
            shippingVo.setReceiverName(shipping.getReceiverName());
            shippingVo.setReceiverAddress(shipping.getReceiverAddress());
            shippingVo.setReceiverCity(shipping.getReceiverCity());
            shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
            shippingVo.setReceiverMobile(shipping.getReceiverMobile());
            shippingVo.setReceiverPhone(shipping.getReceiverPhone());
            shippingVo.setReceiverProvince(shipping.getReceiverProvince());
            shippingVo.setReceiverZip(shipping.getReceiverZip());

            orderVo.setShippingVo(shippingVo);
        }

        orderVo.setPaymentTime(order.getPaymentTime());
        orderVo.setCloseTime(order.getCloseTime());
        orderVo.setCreateTime(order.getCreateTime());
        orderVo.setEndTime(order.getEndTime());
        orderVo.setSendTime(order.getSendTime());
        orderVo.setUpdateTime(order.getUpdateTime());

        orderVo.setImageHost(PropertiesUtils.getProperty(Constant.IMAGE_HOST));

        List<OrderItemVo> orderItemVos = orderItems.stream().map(item -> new OrderItemVo(item)).collect(Collectors.toList());
        orderVo.setOrderItemVos(orderItemVos);

        return orderVo;
    }

    private long generateOrderNo(){
        long currentTime = System.currentTimeMillis();
        return currentTime + currentTime % ((long) Math.random() * 10);
    }

    private ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId,List<Cart> carts){
        List<OrderItem> orderItems = Lists.newArrayList();

        if (CollectionUtils.isEmpty(carts))
            return ServerResponse.createByErrorMessage("购物车为空");

        for (Cart cart : carts){
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.getById(cart.getProductId());

            if (Constant.ProductStatusEnum.ON_SALE.getCode() != product.getStatus())
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "不在售卖状态");

            if (cart.getQuantity() > product.getStock())
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "库存不足");

            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),cart.getQuantity()));
            orderItems.add(orderItem);
        }

        return ServerResponse.createBySuccess(orderItems);
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }
}
