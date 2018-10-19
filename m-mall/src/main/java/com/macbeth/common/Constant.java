package com.macbeth.common;

import com.google.common.collect.Sets;
import com.macbeth.util.PropertiesUtils;
import lombok.Data;

import java.util.Arrays;
import java.util.Set;

public class Constant {
    public static final String CURRENT_USER = "current_user";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";
    public static final String TOKEN_PREFIX = "token_";
    public static final String IMAGE_HOST = "ftp.server.http.prefix";

    public static final Long LOCK_TIME = Long.parseLong(PropertiesUtils.getProperty("lock.time","5000"));

    public static final Integer CLOSE_ORDER_SCOPE = Integer.parseInt(PropertiesUtils.getProperty("close.order.scope","2"));

    public static final String COOKIE_DOMAIN = PropertiesUtils.getProperty("cookie_domain","macbeth.com.cn");
    public static final String COOKIE_NAME = PropertiesUtils.getProperty("cookie.name","macbeth_login_token");

    public static final Integer REDIS_MAX_TOTAL = Integer.parseInt(PropertiesUtils.getProperty("redis.max.total","20"));
    public static final Integer REDIS_MAX_IDLE = Integer.parseInt(PropertiesUtils.getProperty("redis.max.idle","10"));
    public static final Integer REDIS_MIN_IDLE = Integer.parseInt(PropertiesUtils.getProperty("redis.min.idle","2"));
    public static final Boolean REDIS_TEST_ON_BORROW = Boolean.valueOf(PropertiesUtils.getProperty("redis.test.on.borrow","true"));
    public static final Boolean REDIS_TEST_ON_RETURN = Boolean.valueOf(PropertiesUtils.getProperty("redis.test.on.return","true"));

    public static final String REDIS_1_IP = PropertiesUtils.getProperty("redis.1.ip");
    public static final Integer REDIS_1_PORT = Integer.parseInt(PropertiesUtils.getProperty("redis.1.port","6379"));
    public static final String REDIS_1_PASSWORD = PropertiesUtils.getProperty("redis.1.password");

    public static final String REDIS_2_IP = PropertiesUtils.getProperty("redis.2.ip");
    public static final Integer REDIS_2_PORT = Integer.parseInt(PropertiesUtils.getProperty("redis.2.port","6379"));
    public static final String REDIS_2_PASSWORD = PropertiesUtils.getProperty("redis.2.password");


    public static final Integer REDIS_TIMEOUT = Integer.parseInt(PropertiesUtils.getProperty("redis.timeout","2000"));

    public interface REDIS_LOCK{
        String CLOSE_ORDER_TASK_LOCK = "CLOSE_ORDER_TASK_LOCK";
    }

    public interface Cart{
        int CHECKED = 1;//购物车选中状态
        int UN_CHECKED = 0;//购物车未选中状态

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }
    public interface Role{
        int ROLE_CUSTOMER = 1; //普通用户
        int ROLE_ADMIN = 0; // 管理员
    }

    public enum ProductStatusEnum {
        ON_SALE("在售",1);

        private String value;
        private int code;
        ProductStatusEnum(String value,int code){
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    public enum OrderStatus{
        CANCELL(0,"已取消"),WAIT_FOR_PAY(10,"未付款"),HAD_PAY(20,"已付款"),SHIPPED(40,"已发货"),SUCCESS(50,"交易成功"),CLOSED(60,"交易关闭");
        private String value;
        private int code;
        OrderStatus(int code,String value){
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatus getByCode(int code){
            return Arrays.stream(OrderStatus.values()).filter(item -> item.getCode() == code).findAny().get();
        }
    }

    public interface AlipayCallBack{
        String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public enum PayPlatform{
        ALIPAY(1,"支付宝");
        private String value;
        private int code;
        PayPlatform(int code,String value){
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum PaymentTypeEnum{
        ONLINE_PAY(1,"在线支付");
        private String value;
        private int code;
        PaymentTypeEnum(int code,String value){
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static PaymentTypeEnum getByCode(int code){
            return Arrays.stream(PaymentTypeEnum.values()).filter(item -> item.getCode() == code).findAny().get();
        }
    }
}
