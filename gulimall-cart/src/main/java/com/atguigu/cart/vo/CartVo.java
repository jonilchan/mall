package com.atguigu.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVo {

    private List<CartItemVo> items;

    //商品数量
    private Integer countNum;

    //商品类型数量
    private Integer countType;

    //商品总价
    private BigDecimal totalAmount;

    //减免价格
    private BigDecimal reduce = new BigDecimal("0");

    public Integer getCountNum(){
        int count = 0;
        if (items != null && items.size() > 0){
            for (CartItemVo item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public Integer getCountType(){
        return items.size();
    }

    public BigDecimal getTotalAmount(){
        BigDecimal amount = new BigDecimal("0");
        if (items != null && items.size() > 0){
            for (CartItemVo item : items) {
                amount = amount.add(item.getTotalPrice());
            }
        }
        return amount.subtract(getReduce());
    }
}
