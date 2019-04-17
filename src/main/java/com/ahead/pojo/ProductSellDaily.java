package com.ahead.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 店铺中每个商品每天的消费统计
 * @author Yang
 * @version 1.0
 * @time 2019/3/5
 */
@Data
public class ProductSellDaily {

    private Long ProductSellDailyId;
    /**
     * 哪天的销量，精确到天
     */
    private Date createTime;

    /**
     * 销量
     */
    private Integer total;

    /**
     * 哪个商品的销量
     */
    private Product product;

    /**
     * 哪个店铺的销量
     */
    private Shop shop;

}
