package com.ahead.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 用户信息
 *
 * @author Yang
 */
@Data
public class User {

    private Long userId;
    /**
     * 用户昵称
     */
    private String name;

    /**
     * 用户头像地址
     */
    private String profileImg;
    private String email;
    private String gender;

    /**
     * 启用状态 0禁止使用本商城，1允许使用本商城
     */
    private Integer enableStatus;

    /**
     * 用户类型 0:超级管理员 1:顾客 2:店家 (如果是店员的话且他不是顾客或者店家，该值就为null，如果再点击前台（本身是店家就不用修改）就修改为顾客，如果点击了店家就修改为店家)
     */
    private Integer userType;
    private Date createTime;
    private Date lastEditTime;



}
