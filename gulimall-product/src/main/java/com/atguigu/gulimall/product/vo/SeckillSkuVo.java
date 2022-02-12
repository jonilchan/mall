package com.atguigu.gulimall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillSkuVo {

    //活动id
    private Long promotionId;
    //活动场次
    private Long promotionSessionId;
    //商品Id
    private Long skuId;
    //秒杀价格
    private BigDecimal seckillProce;
    //秒杀商品总量
    private Integer seckillCount;
    //没人限购数量
    private Integer seckillLimit;
    //排序
    private Integer seckillSort;
    //当前商品秒杀的开始时间
    private Long startTime;
    //当前商品秒杀的结束世界
    private Long endTime;
    //秒杀商品的随机码
    private String randomCode;

}
