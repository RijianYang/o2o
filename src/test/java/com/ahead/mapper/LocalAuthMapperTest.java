//package com.ahead.mapper;
//
//import com.ahead.BaseTest;
//import com.ahead.pojo.LocalAuth;
//import com.ahead.pojo.User;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.Date;
//
///**
// * @author Yang
// * @version 1.0
// * @time 2019/3/1
// */
//public class LocalAuthMapperTest extends BaseTest {
//
//    @Autowired
//    private LocalAuthMapper localAuthMapper;
//
//    @Test
//    public void testInsertLocalAuth() {
//        LocalAuth localAuth = new LocalAuth();
//        localAuth.setUsername("测试平台账号");
//        localAuth.setPassword("12345");
//        localAuth.setCreateTime(new Date());
//        localAuth.setLastEditTime(new Date());
//
//        User user = new User();
//        user.setUserId(1L);
//        localAuth.setUser(user);
//
//        localAuthMapper.insertLocalAuth(localAuth);
//        System.out.println(localAuth.getLocalAuthId());
//    }
//
//    @Test
//    public void testSelectLocalAuthByUserNameAndPassword() {
//        LocalAuth localAuth = new LocalAuth();
//        localAuth.setUsername("测试平台账号");
//        localAuth.setPassword("12345");
//        LocalAuth l = localAuthMapper.selectLocalAuthByUserNameAndPassword(localAuth);
//        System.out.println(l);
//    }
//
//    @Test
//    public void teStselectLocalAuthByUserId() {
//        LocalAuth localAuth = localAuthMapper.selectLocalAuthByUserId(1L);
//        System.out.println(localAuth);
//    }
//
//    @Test
//    public void testUpdateLocalAuth() {
//        LocalAuth localAuth = new LocalAuth();
//        User user = new User();
//        user.setUserId(1L);
//        localAuth.setUser(user);
//        localAuth.setUsername("测试平台账号");
//        localAuth.setPassword("qwert");
//        localAuth.setLastEditTime(new Date());
//        localAuthMapper.updateLocalAuthPassword(localAuth, "12345");
//    }
//}
