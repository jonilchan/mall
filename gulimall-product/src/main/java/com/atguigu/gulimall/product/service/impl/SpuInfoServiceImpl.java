package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.dao.SpuInfoDao;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Resource
    private SpuInfoDescService spuInfoDescService;

    @Resource
    private SpuImagesService spuImagesService;

    @Resource
    private AttrService attrService;

    @Resource
    private ProductAttrValueService productAttrValueService;

    @Resource
    private SkuInfoService skuInfoService;

    @Resource
    private SkuImagesService skuImagesService;

    @Resource
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Resource
    private CouponFeignService couponFeignService;

    @Resource
    private BrandService brandService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private WareFeignService wareFeignService;

    @Resource
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuSaveVo vo) {
        //保存spu基本信息
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(infoEntity);

        //保存spu的描述图片
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(descEntity);

        //保存spu的图片集
        List<String> images = vo.getImages();
        spuImagesService.saveImages(infoEntity.getId(), images);

        //保存spu的规格参数
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity id = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(id.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(infoEntity.getId());
            return valueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);

        //保存spu的积分信息
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(infoEntity.getId());
        R r1 = couponFeignService.saveSpuBounds(spuBoundTo);
        if (r1.getCode() != 0){
            log.error("远程保存积分信息失败");
        }
        //保存spu对应的所有sku信息
        //保存sku的基本信息
        List<Skus> skus = vo.getSkus();
        if (skus != null && skus.size() > 0){
            skus.forEach(item ->{
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1){
                        defaultImg = image.getImgUrl();
                    }
                }

                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);

                //保存sku的图片信息
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> skuImagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> !StringUtils.isEmpty(entity.getImgUrl())
                ).collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntities);

                //保存sku的销售属性信息
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);
                    return attrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                //保存sku的优惠、满减等信息
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                    R r2 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r2.getCode() != 0){
                        log.error("远程保存优惠信息失败");
                    }
                }
            });
        }



    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)){
            wrapper.and(w -> w.eq("id", key).or().like("spu_name", key));
        }
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status", status);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id", brandId);
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catelog_id", catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void up(Long spuId) {


        //查出当前spuId对应的所有sku信息、品牌的名字
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);
        List<Long> skuIdList = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());


        //查询当前sku中所有可以被检索的规格属性
        List<ProductAttrValueEntity> baseAttrs = productAttrValueService.baseAttrListForSpu(spuId);
        List<Long> attrIds = baseAttrs.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());

        List<Long> searchAttrIds = attrService.selectSearchAttrIds(attrIds);


        List<SkuEsModel.Attrs> attrsList = baseAttrs.stream().filter(item -> searchAttrIds.contains(item.getAttrId())).map(item -> {
            SkuEsModel.Attrs tempAttr = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, tempAttr);
            return tempAttr;
        }).collect(Collectors.toList());

        //发送远程调用，在库存系统中查询是否有库存

        Map<Long, Boolean> stockMap = null;

        try {
            R skusStock = wareFeignService.getSkusHasStock(skuIdList);
            stockMap = (LinkedHashMap) ((ArrayList<Object>) skusStock.get("data")).get(2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //封装每个sku的信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> upProducts = skus.stream().map(sku -> {
            //组装需要的数据
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, esModel);
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());

            //设置库存信息
            if (finalStockMap == null) {
                esModel.setHasStock(true);
            } else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }


            //查询热度评分
            esModel.setHotScore(0L);

            //查询品牌和分类的名字信息
            BrandEntity brandEntity = brandService.getById(esModel.getBrandId());
            esModel.setBrandName(brandEntity.getName());
            esModel.setBrandImg(brandEntity.getLogo());

            CategoryEntity categoryEntity = categoryService.getById(sku.getCatalogId());
            esModel.setCatalogId(categoryEntity.getCatId());
            esModel.setCatalogName(categoryEntity.getName());

            //设置检索属性
            esModel.setAttrs(attrsList);

            return esModel;
        }).collect(Collectors.toList());

        //将数据发送给es进行保存
        R r = searchFeignService.productStatusUp(upProducts);
        if (r.getCode() == 0){
            //修改spu状态
            this.baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.UP_SPU.getCode());
        }else {
            // 应该重新尝试调用接口
        }
    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {

        //先查询sku表里的数据
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);

        //获得spuId
        Long spuId = skuInfoEntity.getSpuId();

        //再通过spuId查询spuInfo信息表里的数据
        SpuInfoEntity spuInfoEntity = this.baseMapper.selectById(spuId);

        //查询品牌表的数据获取品牌名
        BrandEntity brandEntity = brandService.getById(spuInfoEntity.getBrandId());
        spuInfoEntity.setBrandName(brandEntity.getName());

        return spuInfoEntity;
    }

}