//package com.ahead.service;
//
//import com.ahead.BaseTest;
//import com.ahead.dto.O2oExecution;
//import com.ahead.dto.WechatUser;
//import com.ahead.pojo.WechatAuth;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
///**
// * @author Yang
// * @version 1.0
// * @time 2019/2/26
// */
//public class WechatAuthServiceTest extends BaseTest {
//    @Autowired
//    private WechatAuthService wechatAuthService;
//
//    @Test
//    public void testSaveWechatAuth() throws Exception {
//        WechatUser wechatUser = new WechatUser();
//        wechatUser.setHeadimgurl("test");
//        wechatUser.setNickName("测试一下");
//        wechatUser.setOpenId("sdfsdafsdasdfds");
//        wechatUser.setSex(1);
//        O2oExecution<WechatAuth> o2oExecution = wechatAuthService.saveWechatAuth(wechatUser, "1");
//        WechatAuth wechatAuth = o2oExecution.getT();
//        System.out.println(wechatAuth.getUser().getName());
//    }
//}
