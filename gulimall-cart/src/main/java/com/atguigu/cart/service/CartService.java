package com.atguigu.cart.service;

import com.atguigu.cart.vo.CartItemVo;
import com.atguigu.cart.vo.CartVo;

import java.util.List;
import java.util.concurrent.ExecutionException;


public interface CartService {

    CartVo getCart() throws ExecutionException, InterruptedException;

    List<CartItemVo> getUserCartItems();

    CartItemVo addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItemVo getCartItem(Long skuId);

    void clearCartInfo(String cartKey);

    void checkItem(Long skuId, Integer check);

    void changeItemCount(Long skuId, Integer num);

    void deleteIdCartInfo(Integer skuId);
}
