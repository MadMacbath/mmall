package com.macbeth.dao;

import com.macbeth.pojo.PayInfo;

public interface PayInfoMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(PayInfo record);

    int insertSelective(PayInfo record);

    int updateByPrimaryKeySelective(PayInfo record);

    int updateByPrimaryKey(PayInfo record);

}