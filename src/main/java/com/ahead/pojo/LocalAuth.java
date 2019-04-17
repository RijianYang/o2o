package com.ahead.pojo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.util.Date;
/**
 * 平台账号（用来做登录用的）
 * @author Yang
 *
 */
@Data
public class LocalAuth {
	/**
	 * 下面两个类只用来做分组
	 */
	public interface BindWechatAuth{}
	public interface Login{}
	/**
	 * 主键
	 */
	private Long localAuthId;

	/**
	 * 登录账号
	 */
	@NotEmpty(message = "请填写用户名！", groups = {BindWechatAuth.class, Login.class})
	@Length(max = 10, min = 2, message = "用户名为2到10位之间！", groups = { BindWechatAuth.class})
	private String username;

	/**
	 * 登录密码
	 */
	@NotEmpty(message = "请填写密码！", groups = { BindWechatAuth.class, Login.class})
	@Length(max = 10, min = 5, message = "密码为5到10位之间！", groups = {BindWechatAuth.class})
	private String password;
	private Date createTime;
	private Date lastEditTime;

	/**
	 * 属于哪个用户，实现与微信的绑定
	 */
	private User user;


}
