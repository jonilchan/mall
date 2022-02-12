package com.atguigu.gulimall.search.controller;

import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.service.ProductSaveService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/search/save")
public class EsSaveController {

    @Resource
    private ProductSaveService productSaveService;

    /**
     * 商家商品
     */
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels){

        boolean flag = false;

        try {
            flag = productSaveService.productStatusUp(skuEsModels);

        } catch (Exception e) {
            log.error("ElasticSearch商品上架错误：{}", e);
        }

        if (flag){
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }else{
            return R.ok();
        }
    }

}
