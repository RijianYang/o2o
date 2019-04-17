package com.ahead.mapper;

import com.ahead.pojo.ShopCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/19
 */
public interface ShopCategoryMapper {

    /**
     * 根据条件查询出对应的店铺分类列表<br/>
     * 1、如果shopCategoryWhere为空：查出parentId为空的店铺类别集合（所有的一级店铺类别） <br/>
     * 2、如果shopCategoryWhere不为空：查出parentId不为空的店铺类别集合（拼装条件：所有的二级店铺类别）<br/>
     * 3、如果shopCategoryWhere不为空，且shopCategoryWhere.parent不为空，shopCategoryWhere.parent.shopCategoryId不为空（拼装条件：该一级类别下有多少个二级类别）
     * @param shopCategoryWhere
     * @return
     */
    List<ShopCategory> getShopCategoryListByWhere(@Param("shopCategoryWhere")ShopCategory shopCategoryWhere);
}
