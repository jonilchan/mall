package com.atguigu.gulimall.ware.service;

import com.atguigu.gulimall.ware.vo.FareVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.WareInfoEntity;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author jonil
 * @email jonilchan@foxmail.com
 * @date 2022-01-20 13:22:25
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    FareVo getFare(Long addrId);
}

