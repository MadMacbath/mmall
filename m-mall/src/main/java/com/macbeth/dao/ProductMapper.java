package com.macbeth.dao;

import com.macbeth.pojo.Product;
import com.macbeth.pojo.ProductExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    int updateByExampleSelective(@Param("record") Product record, @Param("example") ProductExample example);

    int updateByExample(@Param("record") Product record, @Param("example") ProductExample example);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    Product getById(Integer productId);

    int updateStatusById(@Param("productId") Integer productId, @Param("status") Integer status);

    List<Product> listProducts();

    List<Product> searchProducts(@Param("name") String name, @Param("productId") Integer productId);

    List<Product> selectByNameAndCategoryIds(@Param("name") String name, @Param("categoryIds") List<Integer> categoryIds);

    Integer getStockByProductId(Integer productId);
}