package com.ahead.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 店铺
 *
 * @author Yang
 */
@Data
public class Shop {

    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺描述
     */
    private String shopDesc;

    /**
     * 店铺地址
     */
    private String shopAddr;

    /**
     * 店铺联系电话
     */
    private String phone;

    /**
     * 店铺门面照
     */
    private String shopImg;

    /**
     * 权重
     */
    private Integer priority;

    private Date createTime;

    private Date lastEditTime;

    /**
     * 超级管理员对店铺的审核状态，-1:不可用 0:审核中 1:可用
     */
    private Integer enableStatus;

    private String advice;

    /**
     * 该店铺属于哪个区域
     */
    private Area area;

    /**
     * 该店铺是谁创建的
     */
    private User owner;

    /**
     * 该店铺属于哪种类型
     */
    private ShopCategory shopCategory;


}
