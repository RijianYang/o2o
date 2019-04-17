package com.ahead.service;

import com.ahead.dto.O2oExecution;
import com.ahead.dto.WechatUser;
import com.ahead.pojo.WechatAuth;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/2/25
 */
public interface WechatAuthService {

    /**
     * 注册微信账号
     * @param wechatUser
     * @param state
     * @return
     */
   O2oExecution<WechatAuth> saveWechatAuth(WechatUser wechatUser, String state);

    /**
     * 通过openId获取微信账号
     * @param openId
     * @return
     */
   O2oExecution<WechatAuth> getWechatByOpenId(String openId);
}
