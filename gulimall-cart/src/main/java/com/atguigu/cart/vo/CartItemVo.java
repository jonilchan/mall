package com.atguigu.cart.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartItemVo {

    private Long skuId;

    private Boolean check = true;

    private String title;

    private String image;

    private List<String> skuAttrValues;

    private BigDecimal price;

    private Integer count;

    private BigDecimal totalPrice;

    /**
     * 计算购物总价
     */
    public BigDecimal getTotalPrice(){
        return this.price.multiply(new BigDecimal("" + this.count));
    }
}
