package com.atguigu.gulimall.product.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class AttrRespVo extends AttrVo{
    /**
     * 所属分类名字
     */
    private String catelogName;
    /**
     * 所属分组名字
     */
    private String groupName;
    /**
     * 目录路径
     */
    private Long[] catelogPath;
}
