package com.atguigu.gulimall.search.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchParam {

    //页面传递的全文匹配搜索关键字
    private String keyword;
    //三级分类id
    private Long catalog3Id;
    /**
     * 排序条件
     * saleCount_asc/desc  销量升序/降序
     * skuPrice_asc/desc  价格升序/降序
     * hotScore_asc/desc  热度分升序/降序
     */
    private String sort;
    //是否有货
    private Integer hasStock;
    //价格区间
    private String skuPrice;
    //品牌Id，可以多查询
    private List<Long> brandId;
    //属性，可以多查询
    private List<String> attrs;
    //页数
    private Integer pageNum = 1;
    //原生的所有查询条件
    private String _queryString;

}
