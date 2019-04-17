package com.ahead.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 店铺授权信息
 * @author Yang
 * @version 1.0
 * @time 2019/3/5
 */
@Data
public class ShopAuthMap {
    private Long shopAuthMapId;

    /**
     * 职称
     */
    private String title;

    /**
     * 职称符号（可用于权限控制）
     * 0为店家，1为员工
     */
    private Integer titleFlag;

    /**
     * 授权有效状态  0：无效 1：有效
     */
    private Integer enableStatus;
    private Date createTime;
    private Date lastEditTime;

    /**
     * 授权给哪个员工
     */
    private User employee;

    /**
     * 授权是哪个店铺
     */
    private Shop shop;



}
