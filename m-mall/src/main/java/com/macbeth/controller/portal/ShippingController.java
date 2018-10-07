package com.macbeth.controller.portal;

import com.macbeth.common.ServerResponse;
import com.macbeth.pojo.Shipping;
import com.macbeth.pojo.User;
import com.macbeth.service.ShippingService;
import com.macbeth.to.shipping.ShippingAdd;
import com.macbeth.to.shipping.ShippingUpdate;
import com.macbeth.util.ControllerUtils;
import com.macbeth.util.ObjectUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Api(tags = "收货地址接口")
@RestController
public class ShippingController {

    @Autowired
    private ShippingService shippingService;

    @ApiOperation(value = "创建收货地址")
    @PostMapping(value = "shipping")
    public ServerResponse<Integer> add(@Valid @RequestBody ShippingAdd shippingAdd,
                              @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        Shipping shipping = new Shipping();
        shipping.setUserId(user.getId());
        ObjectUtils.transferEntity(shipping,shippingAdd);
        return shippingService.add(shipping);
    }

    @ApiOperation(value = "删除收货地址")
    @DeleteMapping(value = "shipping/{shippingId}")
    public ServerResponse delete(@ApiIgnore HttpSession session,
                                 @ApiParam(value = "收货地址id",name = "shippingId") @PathVariable("shippingId") Integer shippingId){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        return shippingService.delete(user.getId(),shippingId);
    }

    @ApiOperation(value = "更新收货地址")
    @PutMapping(value = "shipping")
    public ServerResponse update(@Valid @RequestBody ShippingUpdate shippingUpdate,
                                 @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        Shipping shipping = new Shipping();
        shipping.setUserId(user.getId());
        ObjectUtils.transferEntity(shipping,shippingUpdate);
        return shippingService.update(shipping);
    }

    @ApiOperation(value = "查询收货地址")
    @GetMapping(value = "shipping/{shippingId}")
    public ServerResponse select(@ApiParam(value = "收货地址ID",name = "shippingId") @PathVariable("shippingId") Integer shippingId,
                                 @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        return shippingService.select(user.getId(),shippingId);
    }

    @ApiOperation(value = "查询收获地址列表")
    @GetMapping(value = "shippings")
    public ServerResponse list(@ApiParam(value = "当前页",name = "pageNum") @RequestParam(defaultValue = "1") Integer pageNum,
                               @ApiParam(value = "页容量",name = "pageSize") @RequestParam(defaultValue = "10") Integer pageSize,
                               @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError())
            return response;
        User user = (User) response.getData();
        return shippingService.list(user.getId(),pageNum,pageSize);
    }
}
