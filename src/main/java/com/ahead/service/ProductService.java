package com.ahead.service;

import com.ahead.dto.ImgWrap;
import com.ahead.dto.O2oExecution;
import com.ahead.exceptions.ServiceRuntimeException;
import com.ahead.pojo.Product;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/27
 */
public interface ProductService {

    /**
     * 下架或下架商品
     * @param product
     * @return
     */
    O2oExecution<Product> soldOutOrInProduct(Product product);

    /**
     * 添加商品
     * @param product
     * @param imgWrap
     * @param imgWrapList
     * @return
     */
    O2oExecution<Product> addProduct(Product product, ImgWrap imgWrap, List<ImgWrap> imgWrapList) throws ServiceRuntimeException;

    /**
     * 根据id查询商品信息
     *
     * @param productId
     * @return
     */
    O2oExecution<Product> getProductById(Long productId);

    /**
     * 修改商品信息(包含缩略图和详情图)
     * @param product
     * @param imgWrap
     * @param imgWrapList
     * @return
     * @throws ServiceRuntimeException
     */
    O2oExecution<Product> modifyProduct(Product product, ImgWrap imgWrap, List<ImgWrap> imgWrapList) throws ServiceRuntimeException;

    /**
     * 获得分页商品数据
     * @param shopId
     * @param product
     * @param page
     * @return
     * @throws ServiceRuntimeException
     */
    O2oExecution<Product> getProductListByWhere(Long shopId, Product product, Integer page) throws ServiceRuntimeException;

}
