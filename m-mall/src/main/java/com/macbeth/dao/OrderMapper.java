package com.macbeth.dao;

import com.macbeth.pojo.Order;
import com.macbeth.pojo.OrderExample;
import com.macbeth.to.manager.order.OrderSearch;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

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

    List<Order> selectByUserId(Integer userId);

    List<Order> lists();

    List<Order> managerSearch(OrderSearch orderSearch);

    List<Order> selectByStateAndCreateTime(@Param("status") int status, @Param("time") Date time);
}