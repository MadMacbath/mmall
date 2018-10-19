package com.macbeth.util;

import com.google.common.collect.Lists;
import com.macbeth.pojo.Category;
import com.macbeth.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.temporal.TemporalField;
import java.util.List;

@Slf4j
public class JsonUtils {
    private static ObjectMapper objectMapper = new ObjectMapper();
    static {
        // 对象所有字段全部列入
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);

        // 不将时间转换为时间戳
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);

        // 忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);

        // 日期转换格式 yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // 忽略json中有而对象中没有的字段
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> String obj2String(T obj){
        if (obj == null)
            return null;
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.error("",e);
            return null;
        }
    }

    public static <T> String obj2StringPretty(T obj){
        if (obj == null)
            return null;
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.error("",e);
            return null;
        }
    }

    public static <T> T string2Obj(String str,TypeReference<T> tTypeReference){
        if (StringUtils.isEmpty(str) || tTypeReference == null)
            return null;
        try {
            return tTypeReference.getType().equals(String.class) ? (T) str : objectMapper.readValue(str,tTypeReference);
        } catch (IOException e) {
            log.error("read value is error",e);
            return null;
        }
    }

    public static <T> T string2Obj(String str, Class<?> collectionClass, Class<?> ... classes){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,classes);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (IOException e) {
            log.error("read value error",e);
            return null;
        }
    }

    public static void main(String[] args) {
        User user = new User();
        user.setId(1);
        user.setUsername("macbeth");
        Category category = new Category();
        category.setId(1);
        category.setName("列别");

        List<Object> list = Lists.newArrayList();
        list.add(user);
        list.add(category);

        String result = obj2StringPretty(list);
        log.info(result);

        result = obj2String(list);
        log.info(result);

        List ll = string2Obj(result,List.class,User.class,Category.class);
        log.info("end");

    }
}
