package com.ahead.pojo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

/**
 * 商品
 */
@Data
public class Product {

    private Long productId;

    @NotBlank(message = "请填写商品名称")
    @Length(max = 10, min = 2, message = "商品名称为2-10之间")
    private String productName;

    /**
     * 商品描述
     */
    @NotBlank(message = "请填写商品描述")
    @Length(max = 300, message = "描述最多只能为300")
    private String productDesc;

    /**
     * 商品简略图的地址
     */
    private String imgAddr;

    /**
     * 商品原价
     */
    @NotBlank(message = "请填写商品原价")
    private String normalPrice;

    /**
     * 商品折扣价
     */
    @NotBlank(message = "请填写商品折扣价")
    private String promotionPrice;

    /**
     * 商品对应的积分
     */
    @NotNull(message = "请填写积分")
    private Integer point;

    /**
     * 权重
     */
    @NotNull(message = "请填写商品优先级")
    private Integer priority;

    private Date createTime;
    private Date lastEditTime;

    /**
     * 0:下架 1:在前端展示系统展示
     */
    private Integer enableStatus;

    /**
     * 该商品有多少张商品图片
     */
    private List<ProductImg> productImgList;
    /**
     * 该商品属于哪个分类
     */
    private ProductCategory productCategory;

    /**
     * 该商品属于哪个店铺
     */
    private Shop shop;

}
