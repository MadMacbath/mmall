package com.macbeth.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.macbeth.common.ServerResponse;
import com.macbeth.dao.ShippingMapper;
import com.macbeth.pojo.Shipping;
import com.macbeth.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("shippingService")
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse<Integer> add(Shipping shipping) {
        int rowCount = shippingMapper.insert(shipping);
        if (rowCount <= 0)
            return ServerResponse.createByErrorMessage("收货地址创建失败");
        return ServerResponse.createBySuccess(shipping.getId());
    }

    @Override
    public ServerResponse delete(Integer userId, Integer shippingId) {
        // 一定要用userId防止横向越权
        int rowCount = shippingMapper.deleteByUserIdAndShippingId(userId,shippingId);
        if (rowCount <= 0)
            return ServerResponse.createByErrorMessage("删除收货地址失败");
        return ServerResponse.createBySuccessMessage("删除成功");
    }

    @Override
    public ServerResponse update(Shipping shipping) {
        int rowCount = shippingMapper.updateByUserIdAndShippingId(shipping);
        if (rowCount <= 0)
            return ServerResponse.createByErrorMessage("更新失败");
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse select(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.selectByUserIdAndShippingId(userId,shippingId);
        if (shipping == null)
            return ServerResponse.createByErrorMessage("查询收货地址失败");
        return ServerResponse.createBySuccess(shipping);
    }

    @Override
    public ServerResponse list(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippings = shippingMapper.list(userId);
        PageInfo pageInfo = new PageInfo(shippings);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
