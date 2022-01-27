package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 树形结构返回菜单列表
     * @return 菜单列表
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        //2、组装成父子的树形结构
        List<CategoryEntity> levelMenus = entities.stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0).map(menu -> {
            menu.setChildren(getChildren(menu, entities));
            return menu;
        }).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort()))).collect(Collectors.toList());
        return levelMenus;
    }

    /**
     * 递归查找所有菜单的子菜单
     *
     * @param root 当前菜单
     * @param menu  所有菜单
     * @return 子菜单
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> menu) {
        //1、判断是否相等
        //2、相等就用peek函数操作，然后在peek函数操作中递归使用getChildren()函数，形成父子树形结构
        //3、排序
        return menu.stream().filter(categoryEntity -> Objects.equals(categoryEntity.getParentCid(), root.getCatId()))
                .peek(categoryEntity -> categoryEntity.setChildren(getChildren(categoryEntity, menu)))
                .sorted(Comparator.comparingInt(menu1 -> (menu1.getSort() == null ? 0 : menu1.getSort()))).collect(Collectors.toList());
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 检查当前删除的菜单，是否被别的地方引用

        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths){
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0){
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * 更新所有表中的菜单名字
     * @param category 菜单信息
     */
    @Override
    public void updateCascase(CategoryEntity category) {
        this.baseMapper.updateById(category);
        CategoryBrandRelationEntity relationEntity = new CategoryBrandRelationEntity();
        relationEntity.setCatelogId(category.getCatId());
        relationEntity.setCatelogName(category.getName());
        categoryBrandRelationService.update(relationEntity, new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", category.getCatId()));
    }
}