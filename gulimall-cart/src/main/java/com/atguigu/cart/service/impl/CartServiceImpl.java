package com.atguigu.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.cart.exception.CartExceptionHandler;
import com.atguigu.cart.feign.ProductFeignService;
import com.atguigu.cart.interceptor.CartInterceptor;
import com.atguigu.cart.service.CartService;
import com.atguigu.cart.vo.CartItemVo;
import com.atguigu.cart.vo.CartVo;
import com.atguigu.cart.vo.SkuInfoVo;
import com.atguigu.cart.vo.UserInfoTo;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.utils.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service("CartService")
public class CartServiceImpl implements CartService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ProductFeignService productFeignService;

    @Resource
    private ThreadPoolExecutor executor;


    @Override
    public CartVo getCart() throws ExecutionException, InterruptedException {
        CartVo cartVo = new CartVo();
        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();
        if (userInfoTo.getUserId() != null) {
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
            String tempCartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();

            List<CartItemVo> tempCartItems = getCartItems(tempCartKey);
            if (tempCartItems != null) {
                for (CartItemVo tempCartItem : tempCartItems) {
                    addToCart(tempCartItem.getSkuId(), tempCartItem.getCount());
                }
                clearCartInfo(tempCartKey);
            }

            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);
        } else {
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();

            List<CartItemVo> cartItems = getCartItems(cartKey);
            cartVo.setItems(cartItems);
        }
        return cartVo;
    }

    @Override
    public void clearCartInfo(String cartKey) {
        stringRedisTemplate.delete(cartKey);
    }

    @Override
    public CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {

        BoundHashOperations<String, Object, Object> carOps = getCarOps();

        String productRedisValue = (String) carOps.get(skuId.toString());

        if (StringUtils.isEmpty(productRedisValue)) {

            CartItemVo cartItemVo = new CartItemVo();


            CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {

                //远程调用
                R info = productFeignService.info(skuId);

                String json = JSON.toJSONString(info.get("skuInfo"));
                SkuInfoVo skuInfoVo = JSON.parseObject(json, SkuInfoVo.class);
                cartItemVo.setSkuId(skuInfoVo.getSkuId());
                cartItemVo.setTitle(skuInfoVo.getSkuTitle());
                cartItemVo.setImage(skuInfoVo.getSkuDefaultImg());
                cartItemVo.setPrice(skuInfoVo.getPrice());
                cartItemVo.setCount(num);
            }, executor);


            CompletableFuture<Void> getSkuAttrValuesFuture = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItemVo.setSkuAttrValues(skuSaleAttrValues);

            }, executor);

            CompletableFuture.allOf(getSkuInfoFuture, getSkuAttrValuesFuture).get();

            String cartItemJson = JSON.toJSONString(cartItemVo);
            carOps.put(skuId.toString(), cartItemJson);
            return cartItemVo;
        } else {

            CartItemVo cartItemVo = JSON.parseObject(productRedisValue, CartItemVo.class);
            cartItemVo.setCount(cartItemVo.getCount() + num);

            String cartItemJson = JSON.toJSONString(cartItemVo);
            carOps.put(skuId.toString(), cartItemJson);
            return cartItemVo;
        }

    }

    private BoundHashOperations<String, Object, Object> getCarOps() {

        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();

        String cartKey;

        if (userInfoTo.getUserId() != null) {
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
        } else {
            cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserKey();
        }

        return stringRedisTemplate.boundHashOps(cartKey);
    }

    @Override
    public CartItemVo getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> carOps = getCarOps();

        String redisValue = (String) carOps.get(skuId.toString());

        return JSON.parseObject(redisValue, CartItemVo.class);
    }

    /**
     * 获取购物车数据
     */
    private List<CartItemVo> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> operations = stringRedisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if (values != null && values.size() > 0) {
            return values.stream().map(obj -> {
                String str = (String) obj;
                return JSON.parseObject(str, CartItemVo.class);
            }).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<CartItemVo> getUserCartItems() {

        List<CartItemVo> cartItemVos;

        UserInfoTo userInfoTo = CartInterceptor.toThreadLocal.get();

        if (userInfoTo.getUserId() == null) {
            return null;
        } else {
            String cartKey = CartConstant.CART_PREFIX + userInfoTo.getUserId();
            List<CartItemVo> cartItems = getCartItems(cartKey);
            if (cartItems == null) {
                throw new CartExceptionHandler();
            }
            cartItemVos = cartItems.stream().filter(CartItemVo::getCheck).peek(item -> {
                BigDecimal price = productFeignService.getPrice(item.getSkuId());
                item.setPrice(price);
            }).collect(Collectors.toList());
        }
        return cartItemVos;
    }

    @Override
    public void checkItem(Long skuId, Integer check) {

        CartItemVo cartItem = getCartItem(skuId);

        cartItem.setCheck(check == 1);

        String redisValue = JSON.toJSONString(cartItem);

        BoundHashOperations<String, Object, Object> carOps = getCarOps();
        carOps.put(skuId.toString(), redisValue);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        CartItemVo cartItem = getCartItem(skuId);
        cartItem.setCount(num);

        BoundHashOperations<String, Object, Object> carOps = getCarOps();
        String redisValue = JSON.toJSONString(cartItem);
        carOps.put(skuId.toString(), redisValue);
    }

    @Override
    public void deleteIdCartInfo(Integer skuId) {
        BoundHashOperations<String, Object, Object> carOps = getCarOps();
        carOps.delete(skuId.toString());
    }
}
