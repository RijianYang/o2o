//package com.ahead.service;
//
//import com.ahead.BaseTest;
//import com.ahead.pojo.LocalAuth;
//import com.ahead.pojo.User;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
///**
// * @author Yang
// * @version 1.0
// * @time 2019/3/2
// */
//public class LocalAuthServiceTest extends BaseTest {
//    @Autowired
//    private LocalAuthService localAuthService;
//
//    @Test
//    public void testAddLocalAuth() {
//        LocalAuth localAuth = new LocalAuth();
//        User user = new User();
//        user.setUserId(1L);
//        localAuth.setUser(user);
//        localAuth.setUsername("测试平台账号");
//        localAuth.setPassword("12345");
//        localAuthService.addLocalAuth(localAuth);
//    }
//
//    @Test
//    public void testModifyLocalAuthPassword() {
////        user.userId, username,password，lastEditTime更改密码
//        LocalAuth localAuth = new LocalAuth();
//        User user = new User();
//        user.setUserId(1L);
//        localAuth.setUser(user);
//        localAuth.setUsername("测试平台账号");
//        localAuth.setPassword("12345");
//        localAuthService.modifyLocalAuthPassword(localAuth, "abcde");
//    }
//
//    @Test
//    public void testFindLocalAuthByUserNameAndPassword() {
//        LocalAuth localAuth = new LocalAuth();
//        localAuth.setUsername("测试平台账号");
//        localAuth.setPassword("abcde");
//        localAuthService.findLocalAuthByUserNameAndPassword(localAuth);
//    }
//
//}
