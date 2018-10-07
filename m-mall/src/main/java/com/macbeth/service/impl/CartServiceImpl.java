package com.macbeth.service.impl;

import com.google.common.collect.Lists;
import com.macbeth.common.Constant;
import com.macbeth.common.ServerResponse;
import com.macbeth.dao.CartMapper;
import com.macbeth.dao.ProductMapper;
import com.macbeth.pojo.Cart;
import com.macbeth.pojo.Product;
import com.macbeth.service.CartService;
import com.macbeth.util.BigDecimalUtils;
import com.macbeth.util.PropertiesUtils;
import com.macbeth.vo.CartVo;
import com.macbeth.vo.manager.product.CartProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service("cartService")
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse add(Integer userId, Integer count, Integer productId) {
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        if (cart == null){
            cart = new Cart();
            cart.setQuantity(count);
            cart.setChecked(Constant.Cart.CHECKED);
            cart.setProductId(productId);
            cart.setUserId(userId);
            cartMapper.insert(cart);
        } else {
            cart.setQuantity(count + cart.getQuantity());
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse update(Integer userId, Integer count, Integer productId) {
        Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
        if (cart != null)
            cart.setQuantity(count);
        else
            return ServerResponse.createByErrorMessage("要更新的商品不存在");
        cartMapper.updateByPrimaryKeySelective(cart);
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse delete(Integer userId, String productIds) {
        List<String> productIdList = Arrays.asList(productIds.split(","));
        int rowCount = cartMapper.deleteByUserIdAndProductId(userId,productIdList);
        if (rowCount == 0)
            return ServerResponse.createByErrorMessage("删除失败");
        return ServerResponse.createBySuccessMessage("删除成功");
    }

    @Override
    public ServerResponse list(Integer userId) {
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse selectProduct(Integer userId,Integer productId) {
        Integer checked = 1;
        cartMapper.checkedOrUncheckedProduct(userId,checked,productId);
        CartVo cartVo = (CartVo) list(userId).getData();
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse unCheckedProduct(Integer userId,Integer productId) {
        Integer checked = 0;
        cartMapper.checkedOrUncheckedProduct(userId,checked,productId);
        CartVo cartVo = (CartVo) list(userId).getData();
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse getCartProductCount(Integer userId) {
        int count = cartMapper.getCartProductCount(userId);
        return ServerResponse.createBySuccess(count);
    }

    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        BigDecimal cartTotalPrice = new BigDecimal("0");

        List<CartProductVo> cartProductVos = Lists.newArrayList();

        for (Cart cart : cartList){
            CartProductVo vo = new CartProductVo();
            vo.setId(cart.getId());
            vo.setUserId(userId);
            vo.setProductId(cart.getProductId());

            Product product = productMapper.getById(cart.getProductId());
            vo.setProductMainImages(product.getMainImage());
            vo.setProductName(product.getName());
            vo.setProductSubtitle(product.getSubtitle());
            vo.setProductStatus(product.getStatus());
            vo.setProductPrice(product.getPrice());
            vo.setProductStock(product.getStock());

            int buyLimitCount = 0;
            if (product.getStock() >= cart.getQuantity()){
                vo.setLimitQuantity(Constant.Cart.LIMIT_NUM_SUCCESS);
                buyLimitCount = cart.getQuantity();
            } else {
                vo.setLimitQuantity(Constant.Cart.LIMIT_NUM_FAIL);
                Cart cartQuantity = new Cart();
                cartQuantity.setId(cart.getId());
                cartQuantity.setQuantity(product.getStock());
                cartMapper.updateByPrimaryKeySelective(cartQuantity);
                buyLimitCount = product.getStock();
            }
            vo.setQuantity(buyLimitCount);
            vo.setProductTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),cart.getQuantity()));
            vo.setProductChecked(cart.getChecked());

            if (cart.getChecked() == Constant.Cart.CHECKED)
                cartTotalPrice = BigDecimalUtils.add(cartTotalPrice.doubleValue(),vo.getProductTotalPrice().doubleValue());

            cartProductVos.add(vo);
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVos(cartProductVos);
        cartVo.setAllChecked(getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtils.getProperty(Constant.IMAGE_HOST));
        return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId){
        return cartMapper.getAllCheckedStatus(userId) == 0;
    }
}
