package com.ahead.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 顾客店铺积分映射（顾客在某个店铺的积分）
 * @author Yang
 * @version 1.0
 * @time 2019/3/5
 */
@Data
public class UserShopMap {
    private Long userShopMapId;
    private Date createTime;
    private Date lastEditTime;

    /**
     * 顾客在该店铺的积分
     */
    private Integer point;

    /**
     * 属于哪个顾客
     */
    private User buyer;

    /**
     * 属于哪个店铺
     */
    private Shop shop;
}
