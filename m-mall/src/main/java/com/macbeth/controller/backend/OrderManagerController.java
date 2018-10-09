package com.macbeth.controller.backend;

import com.github.pagehelper.PageInfo;
import com.macbeth.common.ServerResponse;
import com.macbeth.service.OrderService;
import com.macbeth.to.manager.order.OrderSearch;
import com.macbeth.util.ControllerUtils;
import com.macbeth.vo.manager.product.OrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "订单后台接口")
@RestController
@RequestMapping(value = "order-manager")
public class OrderManagerController {

    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "获取订单列表")
    @RequestMapping(value = "orders",method = RequestMethod.GET)
    public ServerResponse<PageInfo<OrderVo>> list(@ApiIgnore HttpSession session,
                                                  @RequestParam(defaultValue = "1") @ApiParam(value = "当前页",name = "pageNum") int pageNum,
                                                  @RequestParam(defaultValue = "10") @ApiParam(value = "页容量",name = "pageSize") int pageSize){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError() || ControllerUtils.isAdminLogin(response).isError())
            return response;

        return orderService.managerList(pageNum,pageSize);
    }


    @ApiOperation(value = "获取订单详情")
    @RequestMapping(value = "order/{orderNo}",method = RequestMethod.GET)
    public ServerResponse<OrderVo> details(@ApiIgnore HttpSession session,
                                                     @PathVariable(value = "orderNo") @ApiParam(value = "订单号",name = "orderNo") Long orderNo){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError() || ControllerUtils.isAdminLogin(response).isError())
            return response;

        return orderService.managerDetail(orderNo);
    }

    @ApiOperation(value = "订单搜索")
    @RequestMapping(value = "orders/search",method = RequestMethod.GET)
    public ServerResponse<PageInfo<List<OrderVo>>> search(@ApiIgnore HttpSession session,
                                                          @RequestBody @Valid OrderSearch orderSearch){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError() || ControllerUtils.isAdminLogin(response).isError())
            return response;

        return orderService.managerSearch(orderSearch);
    }

    @ApiOperation(value = "订单发货")
    @PutMapping(value = "order/{orderNo}/postage")
    public ServerResponse send(@PathVariable("orderNo") @ApiParam(value = "订单号",name = "orderNo") Long orderNo,
                               @ApiIgnore HttpSession session){

        ServerResponse response = ControllerUtils.isLogin(session);
        if (response.isError() || ControllerUtils.isAdminLogin(response).isError())
            return response;

        return orderService.sendPostage(orderNo);
    }

}
