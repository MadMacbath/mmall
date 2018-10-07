package com.macbeth.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtils {
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);
    private static Properties properties;

    static {
        properties = new Properties();
        try {
            properties.load(new InputStreamReader(PropertiesUtils.class.getClassLoader().getResourceAsStream("mmall.properties"),"utf-8"));
        } catch (IOException e) {
            logger.error("配置文件读取异常",e);
        }
    }

    public static String getProperty(String key){
        return properties.getProperty(key.trim());
    }

    public static String getProperty(String key,String defaultValue){
        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value))
            value = defaultValue;
        return value;
    }

}
