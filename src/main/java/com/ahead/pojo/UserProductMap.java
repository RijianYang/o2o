package com.ahead.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 顾客消费商品映射
 * @author Yang
 * @version 1.0
 * @time 2019/3/5
 */
@Data
public class UserProductMap {
    private Long userProductMapId;
    private Date createTime;

    /**
     * 哪个顾客消费的
     */
    private User buyer;

    /**
     * 消费了哪个商品
     */
    private Product product;

    /**
     * 消费的商品属于哪个店铺
     */
    private Shop shop;

    /**
     * 哪个操作员操作的
     */
    private User operator;
}




