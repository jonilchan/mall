package com.atguigu.cart.controller;

import com.atguigu.cart.service.CartService;
import com.atguigu.cart.vo.CartItemVo;
import com.atguigu.cart.vo.CartVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Resource
    private CartService cartService;

    @ResponseBody
    @GetMapping("/currentUserCartItems")
    public List<CartItemVo> getCurrentCartItems(){
        return cartService.getUserCartItems();
    }


    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {

        CartVo cartVo = cartService.getCart();
        model.addAttribute("cart", cartVo);

        return "cartList";
    }

    @GetMapping("/addCartItem")
    public String addCartItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes attributes) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId, num);
        attributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccessPage.html";
    }

    @GetMapping("/addToCartSuccessPage.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model){
        CartItemVo cartItem = cartService.getCartItem(skuId);
        model.addAttribute("cartItem", cartItem);
        return "success";
    }

    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("checked") Integer checked){
        cartService.checkItem(skuId, checked);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num){
        cartService.changeItemCount(skuId, num);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Integer skuId){
        cartService.deleteIdCartInfo(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }
}
