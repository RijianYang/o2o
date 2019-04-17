package com.ahead.service;

import com.ahead.dto.O2oExecution;
import com.ahead.enums.ProductCategoryStateEnum;
import com.ahead.exceptions.ServiceRuntimeException;
import com.ahead.pojo.ProductCategory;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/23
 */
public interface ProductCategoryService {

    /**
     * 通过店铺主键获得该店铺对应的所有商品分类信息
     * @param shopId
     * @return
     */
    O2oExecution<ProductCategory>  getProductCategoryByShopId(Long shopId) throws ServiceRuntimeException;

    /**
     * 批量保存店铺商品类别
     * @param productCategoryList
     * @return
     */
    O2oExecution<ProductCategory> batchSaveProductCategory(List<ProductCategory> productCategoryList) throws ServiceRuntimeException;

    /**
     * 根据类别主键和所属的店铺来删除对应的类别
     * @param productCategoryId
     * @param shopId
     * @return
     */
    O2oExecution<ProductCategory> deleteProductCategory(Long productCategoryId, Long shopId) throws ServiceRuntimeException;
}
