package com.macbeth.dao;

import com.macbeth.pojo.Cart;
import com.macbeth.pojo.CartExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    int updateByExampleSelective(@Param("record") Cart record, @Param("example") CartExample example);

    int updateByExample(@Param("record") Cart record, @Param("example") CartExample example);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    List<Cart> selectByUserId(Integer userId);

    Cart selectByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    int getAllCheckedStatus(Integer userId);

    int deleteByUserIdAndProductId(@Param("userId") Integer userId, @Param("productIdList") List<String> productIdList);

    int checkedOrUncheckedProduct(@Param("userId") Integer userId, @Param("checked") Integer checked,@Param("productId") Integer productId);

    int getCartProductCount(Integer userId);

    List<Cart> querySelectedByUserId(Integer userId);

    int deleteByPrimaryKeys(List<Integer> ids);
}