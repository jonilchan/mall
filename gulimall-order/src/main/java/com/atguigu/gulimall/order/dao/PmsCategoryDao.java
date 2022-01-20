package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.PmsCategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author jonil
 * @email jonilchan@foxmail.com
 * @date 2022-01-20 13:19:10
 */
@Mapper
public interface PmsCategoryDao extends BaseMapper<PmsCategoryEntity> {
	
}
