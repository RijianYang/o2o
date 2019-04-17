package com.ahead.mapper;

import com.ahead.pojo.WechatAuth;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/2/25
 */
public interface WechatAuthMapper {

    /**
     * 保存微信认证
     * @param wechatAuth
     * @return
     */
    int insertWechatAuth(WechatAuth wechatAuth);

    /**
     * 根据opId查询微信认证号
     * @param openId
     * @return
     */
    WechatAuth selectWechatAuthByOpenId(String openId);
}
