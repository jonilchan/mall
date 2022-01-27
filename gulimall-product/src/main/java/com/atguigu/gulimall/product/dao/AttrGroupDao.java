package com.atguigu.gulimall.product.dao;

import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 属性分组
 * 
 * @author jonil
 * @email jonilchan@foxmail.com
 * @date 2022-01-20 12:44:51
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

}
