package com.macbeth.dao;

import com.macbeth.pojo.Order;
import com.macbeth.pojo.OrderExample;
import org.apache.ibatis.annotations.Param;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    int updateByExampleSelective(@Param("record") Order record, @Param("example") OrderExample example);

    int updateByExample(@Param("record") Order record, @Param("example") OrderExample example);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderNoAndUserId(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

    Order selectByOrderNo(String orderNo);
}