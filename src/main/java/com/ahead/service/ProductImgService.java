package com.ahead.service;

import com.ahead.dto.O2oExecution;
import com.ahead.pojo.ProductImg;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/21
 */
public interface ProductImgService {

    O2oExecution<ProductImg> getProductImgListById(Long productId);
}
