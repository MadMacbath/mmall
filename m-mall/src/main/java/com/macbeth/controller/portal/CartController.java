package com.macbeth.controller.portal;

import com.macbeth.common.ServerResponse;
import com.macbeth.pojo.User;
import com.macbeth.service.CartService;
import com.macbeth.to.cart.CartAddProduct;
import com.macbeth.to.cart.CartDeleteProducts;
import com.macbeth.to.cart.CartUpdateProduct;
import com.macbeth.util.ControllerUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;

@Api(tags = "购物车接口")
@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    @ApiOperation(value = "添加商品到购物车")
    @PostMapping(value = "cart")
    public ServerResponse add(@RequestBody CartAddProduct cartAddProduct,
                              @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        return cartService.add(user.getId(),cartAddProduct.getCount(),cartAddProduct.getProductId());
    }

    @ApiOperation(value = "更新购物车")
    @PutMapping(value = "cart")
    public ServerResponse update(@RequestBody CartUpdateProduct cartUpdateProduct,
                                 @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        return cartService.update(user.getId(), cartUpdateProduct.getCount(),cartUpdateProduct.getProductId());
    }

    @ApiOperation(value = "删除购物车")
    @DeleteMapping(value = "cart")
    public ServerResponse delete(@RequestBody CartDeleteProducts cartDeleteProducts,
                                 @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        return cartService.delete(user.getId(),cartDeleteProducts.getProductIds());
    }

    @ApiOperation(value = "查询购物车")
    @GetMapping(value = "cart")
    public ServerResponse list(@ApiIgnore HttpSession session){
        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        return cartService.list(user.getId());
    }

    @ApiOperation(value = "选中购物车全部商品")
    @PutMapping(value = "carts/checked")
    public ServerResponse selectAll(@ApiIgnore HttpSession session){
        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;

        User user = (User) response.getData();
        return cartService.selectProduct(user.getId(),null);
    }

    @ApiOperation(value = "反选所有购物车中的商品")
    @PutMapping(value = "carts/unchecked")
    public ServerResponse uncheckedAll(@ApiIgnore HttpSession session){
        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        return cartService.unCheckedProduct(user.getId(),null);
    }

    @ApiOperation(value = "选中购物车中的某个商品")
    @PutMapping(value = "cart/{productId}/checked")
    public ServerResponse checkedProduct(@ApiParam(value = "产品ID",name = "productId") @PathVariable("productId") Integer productId,
                                         @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        return cartService.selectProduct(user.getId(),productId);
    }

    @ApiOperation(value = "反选购物车中的某个商品")
    @PutMapping(value = "cart/{productId}/unchecked")
    public ServerResponse uncheckedProduct(@ApiParam(value = "产品ID",name = "productId") @PathVariable("productId") Integer productId,
                                         @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        return cartService.unCheckedProduct(user.getId(),productId);
    }

    @ApiOperation(value = "统计购物车中的商品数量")
    @GetMapping(value = "cart/products/count")
    public ServerResponse getCountProductOfCart(@ApiIgnore HttpSession session){
        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return ServerResponse.createBySuccess(0);
        User user = (User) response.getData();
        return cartService.getCartProductCount(user.getId());
    }
}
