package com.macbeth.util;

import com.google.common.collect.Lists;
import com.sun.corba.se.impl.orbutil.ObjectStreamClassUtil_1_3;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

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

    public static void main(String[] args) throws IOException {
        try (
                BufferedReader reader = new BufferedReader(new FileReader("C:\\macbeth\\work_subject\\mmall\\m-mall\\src\\main\\webapp\\WEB-INF\\swagger\\index.html"));
        ){
            String str = "";
            List<String> list = Lists.newArrayList();
            while ((str = reader.readLine()) != null){
                str = str.trim();
                if (str.startsWith("url:"))
                    str = "url: 'test test',";
                list.add(str);
            }
            try(BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\macbeth\\work_subject\\mmall\\m-mall\\src\\main\\webapp\\WEB-INF\\swagger\\index.html"))){
                list.stream().forEach(s -> {
                    try {
                        writer.write(s);
                        writer.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }

    }

}
