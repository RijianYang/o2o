package com.ahead.mapper;

import com.ahead.pojo.ProductImg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/25
 */
public interface ProductImgMapper {

    /**
     * 批量插入详情图片
     * @param
     * @return
     */
    int batchInsertProductImg(List<ProductImg> productImgList);

    /**
     * 根据商品的主键把所有属于该商品的图片全部删除
     * @param productId
     * @return
     */
    int deleteProductImgByProductId(Long productId);

    List<ProductImg> selectProductImgListByProductId(Long productId);
}
