package com.ahead.mapper;

import com.ahead.pojo.ProductSellDaily;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/10
 */
public interface ProductSellDailyMapper {

    /**
     * 根据条件查询出店铺销量的统计数据
     * @param productSellDailyWhere
     * @param beginTime
     * @param endTime
     * @return
     */
    List<ProductSellDaily> selectProductSellDailyListByWhere (@Param("productSellDailyWhere")
                                                                      ProductSellDaily productSellDailyWhere,
                                                              @Param("beginTime") Date beginTime,
                                                              @Param("endTime") Date endTime);

    /**
     * 插入每一天每个店铺中每个商品的销量
     * @return
     */
    int insertProductSellDaily();

    /**
     * 插入前一天没有销量的商品，设置其销量为0
     * @return
     */
    int insertDefaultProductSellDaily();
}