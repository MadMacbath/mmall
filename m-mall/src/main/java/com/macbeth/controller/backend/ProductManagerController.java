package com.macbeth.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.macbeth.common.Constant;
import com.macbeth.common.ServerResponse;
import com.macbeth.pojo.Product;
import com.macbeth.service.FileService;
import com.macbeth.service.ProductService;
import com.macbeth.to.manager.product.ProductAdd;
import com.macbeth.to.manager.product.ProductSearch;
import com.macbeth.to.manager.product.ProductUpdateStatus;
import com.macbeth.util.ControllerUtils;
import com.macbeth.util.ObjectUtils;
import com.macbeth.util.PropertiesUtils;
import com.macbeth.vo.manager.product.ProductDetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Api(tags = "商品管理接口")
@RestController
@RequestMapping(value = "product-manager")
public class ProductManagerController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FileService fileService;

    @ApiOperation(value = "添加商品")
    @RequestMapping(value = "product",method = RequestMethod.POST)
    public ServerResponse<String> addProduct(@Validated @RequestBody ProductAdd productAdd,
                                             @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError() || ControllerUtils.isAdminLogin(response).isError())
            return response;

        Product product = new Product();
        ObjectUtils.transferEntity(product,productAdd);
        return productService.saveOrUpdateProduct(product);
    }

    @ApiOperation(value = "更新产品状态")
    @RequestMapping(value = "product/{productId}/status",method = RequestMethod.PUT)
    public ServerResponse<String> setStatus(@PathVariable("productId") @ApiParam(value = "产品ID",name = "productId") @Valid Integer productId,
                                            @Valid ProductUpdateStatus productUpdateStatus,
                                            @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError() || ControllerUtils.isAdminLogin(response).isError())
            return response;

        return productService.updateStatus(productId,productUpdateStatus.getStatus());
    }

    @ApiOperation(value = "获取产品详情")
    @RequestMapping(value = "product/{productId}",method = RequestMethod.GET)
    public ServerResponse<ProductDetailVo> getProductInformation(@PathVariable("productId") @ApiParam(value = "产品id",name = "productId") Integer productId,
                                                                 @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError() || ControllerUtils.isAdminLogin(response).isError())
            return response;

        return productService.managerGetProductInformation(productId);
    }

    @ApiOperation(value = "获取产品列表")
    @RequestMapping(value = "products",method = RequestMethod.GET)
    public ServerResponse<PageInfo> listProducts(@ApiParam(value = "当前页数",name = "pageNum") @RequestParam(defaultValue = "1") int pageNum,
                                                 @ApiParam(value = "页面容量",name = "pageSize") @RequestParam(defaultValue = "10") int pageSize,
                                                 @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError() || ControllerUtils.isAdminLogin(response).isError())
            return response;

        return productService.listProducts(pageNum,pageSize);
    }

    @ApiOperation(value = "产品搜索")
    @RequestMapping(value = "product",method = RequestMethod.GET)
    public ServerResponse<PageInfo> searchProducts(ProductSearch productSearch,
                                                   @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError() || ControllerUtils.isAdminLogin(response).isError())
            return response;
        return productService.searchProducts(productSearch.getName(),productSearch.getProductId(),productSearch.getPageNum(),productSearch.getPageSize());
    }

    @ApiOperation(value = "文件上传")
    @RequestMapping(value = "file",headers = "content-type=multipart/form-data",method = RequestMethod.POST)
    public ServerResponse uploadFile(@ApiParam(value = "上传的文件",name = "file",required = true) MultipartFile file,
                                     @ApiIgnore HttpServletRequest request){
        HttpSession session = request.getSession();
        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError() || ControllerUtils.isAdminLogin(response).isError())
            return response;
        String path = request.getServletContext().getRealPath("upload");
        String targetFileName = fileService.upload(file,path);
        String url = PropertiesUtils.getProperty(Constant.IMAGE_HOST) + targetFileName;
        Map<String,String> fileMap = Maps.newHashMap();
        fileMap.put("url",url);
        fileMap.put("uri",targetFileName);
        return ServerResponse.createBySuccess(fileMap);
    }
}
