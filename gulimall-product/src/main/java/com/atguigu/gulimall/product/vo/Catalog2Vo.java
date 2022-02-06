package com.atguigu.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//二级分类VO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Catalog2Vo {

    private String catalog1Id;

    private List<Catalog3Vo> catalog3list;

    private String id;

    private String name;

    //三级分类VO
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Catalog3Vo{

        private String catalog2Id;

        private String id;

        private String name;
    }
}
