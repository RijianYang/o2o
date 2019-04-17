package com.ahead.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 商品图片
 */
@Data
public class ProductImg {

    private Long productImgId;
    /**
     * 商品图片地址
     */
    private String imgAddr;

    /**
     * 商品图片的描述
     */
    private String imgDesc;

    private Integer priority;
    private Date createTime;

    /**
     * 该图片属于哪个商品
     */
    private Product product;


}
