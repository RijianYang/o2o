package com.ahead.service.impl;

import com.ahead.dto.O2oExecution;
import com.ahead.mapper.ProductImgMapper;
import com.ahead.pojo.ProductImg;
import com.ahead.service.ProductImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/21
 */
@Service
public class ProductImgServiceImpl implements ProductImgService {

    @Autowired
    private ProductImgMapper productImgMapper;

    /**
     * 根据商品id查询出对应的商品所有的详情图片
     * @param productId
     * @return
     */
    @Override
    public O2oExecution<ProductImg> getProductImgListById(Long productId) {
        O2oExecution<ProductImg> o2oExecution = null;
        List<ProductImg> productImgList = productImgMapper.selectProductImgListByProductId(productId);
        o2oExecution = O2oExecution.isEmpty(o2oExecution, productImgList);
        return o2oExecution;
    }
}
