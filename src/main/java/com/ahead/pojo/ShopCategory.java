package com.ahead.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 店铺类别
 */
@Data
public class ShopCategory {

    private Long shopCategoryId;

    /**
     * 店铺分类名称
     */
    private String shopCategoryName;

    /**
     * 店铺分类的描述
     */
    private String shopCategoryDesc;

    /**
     * 该店铺分类的图片地址
     */
    private String shopCategoryImg;

    /**
     * 权重
     */
    private Integer priority;

    private Date createTime;

    private Date lastEditTime;

    /**
     * 上级节点
     */
    private ShopCategory parent;

}
