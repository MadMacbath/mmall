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
import com.google.common.collect.Maps;
import com.macbeth.common.Constant;
import com.macbeth.common.ServerResponse;
import com.macbeth.dao.OrderItemMapper;
import com.macbeth.dao.OrderMapper;
import com.macbeth.dao.PayInfoMapper;
import com.macbeth.pojo.Order;
import com.macbeth.pojo.OrderItem;
import com.macbeth.pojo.PayInfo;
import com.macbeth.service.OrderService;
import com.macbeth.util.BigDecimalUtils;
import com.macbeth.util.FileUtils;
import com.macbeth.util.PropertiesUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
