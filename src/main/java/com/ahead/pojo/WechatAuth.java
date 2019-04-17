package com.ahead.pojo;

import lombok.Data;

import java.util.Date;
/**
 * 微信标识
 * @author Yang
 *
 */
@Data
public class WechatAuth {

	/**
	 * 主键
	 */
	private Long wechatAuthId;

	/**
	 * 给平台上的唯一标识（微信号关注该平台就会生成与之对应的一个唯一标识）
	 */
	private String openId;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 该微信号属于哪个用户
	 */
	private User user;

}
