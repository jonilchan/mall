package com.atguigu.gulimall.search.vo;

import com.atguigu.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult {

    //查询到的所有商品信息
    private List<SkuEsModel> products;

    //查询到的所有品牌
    private List<BrandVo> brands;

    //查询到的所有属性
    private List<AttrVo> attrs;

    //查询到的所有目录
    private List<CatalogVo> catalogs;

    //导航栏
    private List<Integer> pageNavs;

    //导航栏信息
    private List<NavVo> navs;

    /**
     * 分页信息
     */
    //当前页码
    private Integer pageNum;
    //总记录数
    private Long total;
    //总页码数
    private Integer totalPages;

    /**
     * 品牌信息
     */
    @Data
    public static class BrandVo{

        private Long brandId;

        private String brandName;

        private String brandImg;

    }

    /**
     * 属性信息
     */
    @Data
    public static class AttrVo{

        private Long attrId;

        private String attrName;

        private List<String> attrValue;

    }

    /**
     * 目录信息
     */
    @Data
    public static class CatalogVo{

        private Long catalogId;

        private String catalogName;

    }

    /**
     * 导航栏信息
     */
    @Data
    public static class NavVo{

        private String navName;

        private String navValue;

        private String link;

    }
}
