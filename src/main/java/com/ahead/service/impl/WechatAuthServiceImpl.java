package com.ahead.service.impl;

import com.ahead.dto.O2oExecution;
import com.ahead.dto.WechatUser;
import com.ahead.enums.CommonStateEnum;
import com.ahead.exceptions.ServiceRuntimeException;
import com.ahead.mapper.ShopMapper;
import com.ahead.mapper.UserMapper;
import com.ahead.mapper.UserShopMapMapper;
import com.ahead.mapper.WechatAuthMapper;
import com.ahead.pojo.User;
import com.ahead.pojo.WechatAuth;
import com.ahead.service.WechatAuthService;
import com.ahead.util.wechat.WechatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/2/25
 */
@Service
public class WechatAuthServiceImpl implements WechatAuthService {

    @Autowired
    private WechatAuthMapper wechatAuthMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserShopMapMapper userShopMapMapper;
    @Autowired
    private ShopMapper shopMapper;

    private final String FRONT_END = "1";
    private final String SHOP_MANAGE = "2";

    /**
     * 先根据微信信息中的openId获取对应的WechatAuth(平台微信账号)，如果账号为空，就注册User与WechatAuth，不为空就直接返回成功
     * @param wechatUser
     * @param state
     * @return
     */
    @Override
    public O2oExecution<WechatAuth> saveWechatAuth(WechatUser wechatUser, String state) {
        //从微信信息中的openId获取对应平台中微信账号
        String openId = wechatUser.getOpenId();
        WechatAuth wechatAuth = wechatAuthMapper.selectWechatAuthByOpenId(openId);
        if (wechatAuth == null) {
            wechatAuth = new WechatAuth();
            //如果没有在该平台注册过的话就为他注册
            User user = WechatUtil.wechatUserToUser(wechatUser);
            if (state.equals(FRONT_END)) {
                //如果点击了前台页面就设置为顾客，顾客的话就设置为可用
                user.setUserType(1);
                user.setEnableStatus(1);
            } else if (state.equals(SHOP_MANAGE)){
                //点击了店铺管理后台 设置为店铺管理员
                user.setUserType(2);
                //注册店家管理就设置为不可用，需要超级管理员激活
                user.setEnableStatus(0);
            } else {
                //如果是店铺授权的话就是null 设置为店员
                user.setUserType(null);
            }
            //补全用户其他信息
            user.setCreateTime(new Date());
            user.setLastEditTime(new Date());

            //保存用户信息获取到主键然后设置给该平台上的微信账号
            int effectNumUser = userMapper.insertUser(user);
            if (effectNumUser <= 0) {
                throw new ServiceRuntimeException("添加用户信息失败！");
            }

            wechatAuth.setUser(user);
            wechatAuth.setCreateTime(new Date());
            wechatAuth.setOpenId(wechatUser.getOpenId());
            //保存该平台的微信账号
            int effectNumWechatAuth = wechatAuthMapper.insertWechatAuth(wechatAuth);
            if (effectNumWechatAuth <= 0) {
                throw new ServiceRuntimeException("添加平台微信标识失败！");
            }
        } else {
            //如果之前注册的是店员且该店员又根据微信登录了前台页面就把该用户类型修改为顾客
            User u = wechatAuth.getUser();
            if (u.getUserType() == null && state.equals(FRONT_END)) {
                u.setUserType(1);
            }
            //如果之前是顾客或者是店员 之后又根据微信登录了店家管理页面就把该用户类型修改为店家且设置为不可用
            if ((u.getUserType() == null || u.getUserType() == 1) && state.equals(SHOP_MANAGE)) {
                u.setUserType(2);
                u.setEnableStatus(0);
            }
            int effectNum = userMapper.updateUserById(u);
            if (effectNum <= 0) {
                throw new ServiceRuntimeException("修改用户类型失败！");
            }
        }
        //如果注册过或者通过上面的逻辑处理就直接返回SUCCESS
        O2oExecution<WechatAuth> o2oExecution = new O2oExecution<>(CommonStateEnum.SUCCESS);
        o2oExecution.setT(wechatAuth);
        return o2oExecution;
    }

    @Override
    public O2oExecution<WechatAuth> getWechatByOpenId(String openId) {
        O2oExecution<WechatAuth> o2oExecution = null;
        WechatAuth wechatAuth = wechatAuthMapper.selectWechatAuthByOpenId(openId);
        //判断获取过来的对象是否为空，然后进行相应的处理，这里面会初始化O2oExecution
        o2oExecution = O2oExecution.isEmpty(o2oExecution, wechatAuth);

        return o2oExecution;
    }
}
