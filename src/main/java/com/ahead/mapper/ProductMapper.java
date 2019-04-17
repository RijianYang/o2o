package com.ahead.mapper;

import com.ahead.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/25
 */
public interface ProductMapper {

    /**
     * 插入一个商品返回插入的主键
     * @param product
     * @return
     */
    int insertProduct(Product product);

    /**
     * 修改商品
     * @param product
     * @return
     */
    int updateProductById(Product product);

    /**
     * 根据商品id获取商品信息
     * @param productId
     * @return
     */
    Product selectProductById(Long productId);

    /**
     * 查询店铺中所有已上架的商品
     * @param shopId
     * @return
     */
    List<Product> selectProductListByWhere(@Param("shopId") Long shopId, @Param("product") Product product);

    /**
     * 把属于该分类的所有商品的分类外键置为空
     * @param productCategoryId
     * @return
     */
    int updateProductCategoryToNull(Long productCategoryId);
}
