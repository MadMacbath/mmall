package com.macbeth.dao;

import com.macbeth.pojo.Shipping;
import com.macbeth.pojo.ShippingExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    int updateByExampleSelective(@Param("record") Shipping record, @Param("example") ShippingExample example);

    int updateByExample(@Param("record") Shipping record, @Param("example") ShippingExample example);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByUserIdAndShippingId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    int updateByUserIdAndShippingId(Shipping shipping);

    Shipping selectByUserIdAndShippingId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    List<Shipping> list(Integer userId);

    Shipping getById(Integer shippingId);
}