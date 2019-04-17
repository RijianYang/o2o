package com.ahead.service.impl;

import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.exceptions.ServiceRuntimeException;
import com.ahead.mapper.ProductCategoryMapper;
import com.ahead.mapper.ProductMapper;
import com.ahead.pojo.ProductCategory;
import com.ahead.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/23
 */
@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {
    @Autowired
    private ProductCategoryMapper productCategoryMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public O2oExecution<ProductCategory> getProductCategoryByShopId(Long shopId) {
        O2oExecution<ProductCategory> productCategoryO2oExecution = null;
        List<ProductCategory> productCategoryList = productCategoryMapper.queryProductCategoryListByShopId(shopId);
        productCategoryO2oExecution = O2oExecution.isEmpty(productCategoryO2oExecution, productCategoryList);

        return productCategoryO2oExecution;

    }

    @Override
    public O2oExecution<ProductCategory> batchSaveProductCategory(List<ProductCategory> productCategoryList) {
        for (ProductCategory productCategory : productCategoryList) {
            productCategory.setCreateTime(new Date());
        }
        int effectNum = productCategoryMapper.batchInsertProductCategroy(productCategoryList);
        if (effectNum <= 0) {
            throw new ServiceRuntimeException("批量保存商品类别失败！");
        }
        return new O2oExecution<>(CommonStateEnum.SUCCESS);
    }

    @Override
    public O2oExecution<ProductCategory> deleteProductCategory(Long productCategoryId, Long shopId) throws ServiceRuntimeException {
        if (productCategoryId == null || shopId == null) {
            throw new ServiceRuntimeException("deleteProductCategory参数为空");
        }

        //TODO 先要把属于该商品类别的的商品的外键设置为null起，才能删除该商品类别，不然会报错
        int effectNum1 = productMapper.updateProductCategoryToNull(productCategoryId);
        if (effectNum1 <= 0) {
            throw new ServiceRuntimeException("删除商品类别失败！");
        }
        int effectNum2 = productCategoryMapper.deleteProductCategory(productCategoryId, shopId);
        if (effectNum2 <= 0) {
            throw new ServiceRuntimeException("删除商品类别失败！");
        }
        return new O2oExecution(CommonStateEnum.SUCCESS);

    }
}
