package com.macbeth.util;

import com.macbeth.pojo.User;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectUtils {
    private static List<String> baseTypes = null;

    static {
        baseTypes = new ArrayList<String>(){{
            this.add(Byte.class.getName());this.add(Short.class.getName());
            this.add(Integer.class.getName());this.add(Long.class.getName());
            this.add(Double.class.getName());this.add(Float.class.getName());
            this.add(Boolean.class.getName());this.add(Character.class.getName());
            this.add(BigDecimal.class.getName());this.add(String.class.getName());
        }};
    }

    public static boolean isBaseType(Type type){
        String typeName = type.getTypeName();
        return baseTypes.contains(typeName);
    }

    public static <T,R> T transferEntity(T target,R resource){
        Class<T> targetClass = (Class<T>) target.getClass();
        Arrays.stream(resource.getClass().getDeclaredFields()).filter(filed -> {
            try {
                filed.setAccessible(true);
                return filed.get(resource) != null;
            } catch (IllegalAccessException e) {
                System.out.println(e.getMessage());
            }
            return false;
        }).forEach(filed ->{
            try {
                Field targetField = targetClass.getDeclaredField(filed.getName());
                targetField.setAccessible(true);
                targetField.set(target,filed.get(resource));
            } catch (NoSuchFieldException e) {
                System.out.println("没有这个字段：" + filed.getName());
            } catch (IllegalAccessException e) {
                System.out.println("非法参数：");
            }
        });
        return target;
    }

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
//        UserRegister register = new UserRegister();
//        register.setAnswer("123");
//        register.setEmail("123");
//        register.setId(1);
//
//        User user = new User();
//        ObjectUtils.transferEntity(user,register);
//        System.out.println(user.getAnswer());
//        System.out.println(user.getEmail());
//        System.out.println(user.getId());

        User user = new User();
        user.setUsername("test");
        user.setId(1);
        Field field = user.getClass().getDeclaredField("id");
        field.setAccessible(true);
        Object o = field.get(user);
        isBaseType(o.getClass());

        System.out.println(Integer.class.getName());

    }
}
