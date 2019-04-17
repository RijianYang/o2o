package com.ahead.service;

import com.ahead.dto.O2oExecution;
import com.ahead.pojo.ShopCategory;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/19
 */
public interface ShopCategoryService {

    /**
     * 根据条件查询出店铺分类列表
     * 二级分类才真正和店铺有关联
     * 一级分类是对二级分类的一个抽象(并无店铺有关系)
     * @param shopCategoryWhere
     * @return
     */
    O2oExecution<ShopCategory> getShopCategoryByWhere(ShopCategory shopCategoryWhere) ;
}
