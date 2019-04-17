package com.ahead.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 顾客兑换的奖品映射
 * @author Yang
 * @version 1.0
 * @time 2019/3/5
 */
@Data
public class UserAwardMap {
    private Long userAwardMapId;
    private Date createTime;

    /**
     * 使用状态 0：未领取 1：已领取
     */
    private Integer usedStatus;


    /**
     * 哪个顾客兑换的奖品
     */
    private User buyer;

    /**
     * 兑换哪个奖品
     */
    private Award award;

    /**
     * 要领取的奖品属于哪个店铺
     */
    private Shop shop;

    /**
     * 操作员
     * 业务中兑换奖品需要店员进行扫码，这里需要记录是哪个店员扫的码
     */
    private User operator;
}
