package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.RefundInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 退款信息
 * 
 * @author jonil
 * @email jonilchan@foxmail.com
 * @date 2022-02-22 21:35:39
 */
@Mapper
public interface RefundInfoDao extends BaseMapper<RefundInfoEntity> {
	
}
