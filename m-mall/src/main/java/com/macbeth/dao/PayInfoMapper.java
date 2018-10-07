package com.macbeth.dao;

import com.macbeth.pojo.OrderItem;
import com.macbeth.pojo.OrderItemExample;
import com.macbeth.pojo.PayInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PayInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PayInfo record);

    int insertSelective(PayInfo record);

    int updateByPrimaryKeySelective(PayInfo record);

    int updateByPrimaryKey(PayInfo record);

}