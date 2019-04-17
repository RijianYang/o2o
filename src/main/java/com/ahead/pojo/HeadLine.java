package com.ahead.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 头条
 */
@Data
public class HeadLine {
	/**
	 * id主键
	 */
	private Long lineId;

	/**
	 * 头条名称
	 */
	private String lineName;

	/**
	 * 点击头条图片跳转的链接
	 */
	private String lineLink;

	/**
	 * 头条图片地址
	 */
	private String lineImg;

	/**
	 * 权重
	 */
	private Integer priority;

	/**
	 * 启用状态 0:不可用 1:可用
	 */
	private Integer enableStatus;
	private Date createTime;
	private Date lastEditTime;


}
