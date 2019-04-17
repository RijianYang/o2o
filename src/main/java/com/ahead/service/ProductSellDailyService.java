package com.ahead.service;

import com.ahead.dto.O2oExecution;
import com.ahead.pojo.ProductSellDaily;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/16
 */
@Service
public interface ProductSellDailyService {

    /**
     * 每日定时对所有店铺的商品销量进行统计
     * @return
     */
    O2oExecution<ProductSellDaily> dailyCalculate();

    /**
     * 查出某个店铺下一周的销量记录
     * @param shopId
     * @param beginTime
     * @param endTime
     * @return
     */
    O2oExecution<ProductSellDaily> getProductSellDailyListByShopId(Long shopId, Date beginTime, Date endTime);
}
