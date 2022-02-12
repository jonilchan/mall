package com.atguigu.gulimall.product.vo;

import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuItemVo {

    //sku基本信息
    private SkuInfoEntity info;

    //库存状态
    private boolean hasStock = true;

    //sku的图片信息
    private List<SkuImagesEntity> images;

    //sku的销售属性组合
    private List<SkuItemSaleAttrVo> saleAttr;

    //spu介绍
    private SpuInfoDescEntity desc;

    //spu的规格参数信息
    private List<SpuItemAttrGroupVo> groupAttrs;

    //秒杀商品的优惠信息
    private SeckillSkuVo seckillSkuVo;

}
