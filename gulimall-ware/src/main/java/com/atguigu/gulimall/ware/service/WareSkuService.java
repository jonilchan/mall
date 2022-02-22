package com.atguigu.gulimall.ware.service;

import com.atguigu.common.to.OrderTo;
import com.atguigu.common.to.SkuHasStockTo;
import com.atguigu.common.to.mq.StockLockedTo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.vo.WareSkuLockVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author jonil
 * @email jonilchan@foxmail.com
 * @date 2022-01-20 13:22:25
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockTo> getSkusHasStock(List<Long> skuIds);

    /**
     * 锁定库存
     * @param vo
     * @return
     */
    boolean orderLockStock(WareSkuLockVo vo);


    /**
     * 解锁库存
     * @param to
     */
    void unlockStock(StockLockedTo to);

    /**
     * 解锁订单
     * @param orderTo
     */
    void unlockStock(OrderTo orderTo);
}

