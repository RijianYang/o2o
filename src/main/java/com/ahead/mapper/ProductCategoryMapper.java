package com.ahead.mapper;

import com.ahead.pojo.ProductCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/23
 */
public interface ProductCategoryMapper {

    /**
     * 根据店铺id查询该店铺下的所有店铺类别
     * @param shopId
     * @return
     */
    List<ProductCategory> queryProductCategoryListByShopId(Long shopId);

    /**
     * 批量插入多条店铺商品类别信息
     * @param productCategoryList
     * @return
     */
    int batchInsertProductCategroy(List<ProductCategory> productCategoryList);

    /**
     * 根据主键和所属的店铺删除对应的商品类别，怕误删别的店铺的类别，所以这里加上店铺更加保险
     * @param productCategoryId
     * @param shopId
     * @return
     */
    int deleteProductCategory(@Param("productCategoryId") Long productCategoryId, @Param("shopId") Long shopId);
}
