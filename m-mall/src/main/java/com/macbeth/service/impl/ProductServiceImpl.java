package com.macbeth.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.macbeth.common.Constant;
import com.macbeth.common.ServerResponse;
import com.macbeth.dao.CategoryMapper;
import com.macbeth.dao.ProductMapper;
import com.macbeth.pojo.Category;
import com.macbeth.pojo.Product;
import com.macbeth.service.CategoryService;
import com.macbeth.service.ProductService;
import com.macbeth.util.ObjectUtils;
import com.macbeth.util.PropertiesUtils;
import com.macbeth.vo.manager.product.ProductDetailVo;
import com.macbeth.vo.manager.product.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("productService")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryService categoryService;

    @Override
    public ServerResponse<String> saveOrUpdateProduct(Product product) {

        Integer id = product.getId();
        String subImages = product.getSubImages();
        if (subImages != null){
            product.setMainImage(subImages.substring(0,subImages.indexOf(",")));
        }
        Category category = categoryService.getCategoryById(product.getCategoryId());
        if (category == null) {
            return ServerResponse.createByErrorMessage("对应的产品类别不存在");
        }
        int rowCount;
        if (id == null){
            rowCount = productMapper.insert(product);
            if (rowCount == 0)
                return ServerResponse.createByErrorMessage("产品创建失败");
            else
                return ServerResponse.createBySuccessMessage("产品创建成功");
        } else {
            rowCount = productMapper.updateByPrimaryKeySelective(product);
            if (rowCount == 0)
                return ServerResponse.createByErrorMessage("产品更新失败");
            else
                return ServerResponse.createBySuccessMessage("产品更新成功");
        }
    }

    @Override
    public ServerResponse<String> updateStatus(Integer productId, Integer status) {
        Product product = productMapper.getById(productId);
        if (product == null)
            return ServerResponse.createByErrorMessage("id没有对应的产品");
        int rowCount = productMapper.updateStatusById(productId,status);
        if (rowCount == 0)
            return ServerResponse.createByErrorMessage("产品状态更新失败");
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<ProductDetailVo> managerGetProductInformation(Integer productId) {
        Product product = productMapper.getById(productId);
        ProductDetailVo vo = new ProductDetailVo();
        ObjectUtils.transferEntity(vo,product);
        String imageHost = PropertiesUtils.getProperty(Constant.IMAGE_HOST);
        vo.setImageHost(imageHost);
        Category category = categoryMapper.selectCategoryById(vo.getCategoryId());
        if (category != null)
            vo.setParentCategoryId(category.getParentId());
        return ServerResponse.createBySuccess(vo);
    }

    @Override
    public ServerResponse<PageInfo> listProducts(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products = productMapper.listProducts();
        List<ProductListVo> productListVos = products.stream()
                .map(product -> ObjectUtils.transferEntity(new ProductListVo(),product))
                .collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVos);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<PageInfo> searchProducts(String name, Integer productId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        if (StringUtils.isNotBlank(name))
            name = "%" + name + "%";

        List<Product> products = productMapper.searchProducts(name,productId);
        List<ProductListVo> productListVos = products.stream()
                .map(product -> ObjectUtils.transferEntity(new ProductListVo(),product))
                .collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo(products);
        pageInfo.setList(productListVos);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<ProductDetailVo> getProductById(Integer productId) {
        Product product = productMapper.getById(productId);
        if (product.getStatus() != Constant.ProductStatusEnum.ON_SALE.getCode())
            return ServerResponse.createByErrorMessage("商品已下架");
        ProductDetailVo vo = ObjectUtils.transferEntity(new ProductDetailVo(),product);
        return ServerResponse.createBySuccess(vo);
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeyWordCategory(String keyWords, Integer categoryId, int pageNum, int pageSize, String orderBy) {
        PageHelper.startPage(pageNum,pageSize);
        if (keyWords == null && categoryId == null)
            return ServerResponse.createByErrorMessage("参数错误");
        List<Integer> categoryIds = Lists.newArrayList();
        if (categoryId != null){
            Category category = categoryMapper.selectCategoryById(categoryId);
            if (category == null){
                PageInfo<ProductDetailVo> pageInfo = new PageInfo<ProductDetailVo>(Lists.newArrayList());
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIds = categoryService.getChildrenRecursionCategory(category.getId()).getData().stream().map(Category::getId).collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(keyWords)){
            keyWords = "%" + keyWords + "%";
        }
        if (StringUtils.isNotBlank(orderBy)) {
            if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyWords) ? null : keyWords,categoryIds);
        List<ProductDetailVo> vos = productList.stream().map(product -> ObjectUtils.transferEntity(new ProductDetailVo(),product)).collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(vos);
        return ServerResponse.createBySuccess(pageInfo);
    }

}
