package com.ahead.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 商品类别
 */
@Data
public class ProductCategory {

	private Long productCategoryId;
	private String productCategoryName;
	/**
	 * //权重
	 */
	private Integer priority;

	/**
	 * 该商品分类属于哪个店铺
	 */
	private Shop shop;
	
	private Date createTime;
	
}
