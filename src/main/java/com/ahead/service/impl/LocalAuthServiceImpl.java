package com.ahead.service.impl;

import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.exceptions.ServiceRuntimeException;
import com.ahead.mapper.LocalAuthMapper;
import com.ahead.pojo.LocalAuth;
import com.ahead.pojo.User;
import com.ahead.service.LocalAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.Objects;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/2
 */
@Service
public class LocalAuthServiceImpl implements LocalAuthService {

    @Autowired
    private LocalAuthMapper localAuthMapper;

    @Override
    public O2oExecution<LocalAuth> findLocalAuthByUserNameAndPassword(LocalAuth localAuth) {
        O2oExecution<LocalAuth> o2oExecution = null;
        //使用MD5加密密码进行查询
        localAuth.setPassword(DigestUtils.md5DigestAsHex(localAuth.getPassword().getBytes()));
        LocalAuth la = localAuthMapper.selectLocalAuthByUserNameAndPassword(localAuth);
        //这里面会根据数据库中返回的对象进行初始化不同的o2oExecution
        o2oExecution = O2oExecution.isEmpty(o2oExecution, la);
        return o2oExecution;
    }

    @Override
    public O2oExecution<LocalAuth> findLocalAuthByUserId(Long userId) {
        O2oExecution<LocalAuth> o2oExecution = null;
        LocalAuth localAuth = localAuthMapper.selectLocalAuthByUserId(userId);
        o2oExecution = O2oExecution.isEmpty(o2oExecution, localAuth);
        return o2oExecution;
    }

    @Override
    public O2oExecution<LocalAuth> addLocalAuth(LocalAuth localAuth, Long userId) {
        O2oExecution<LocalAuth> o2oExecution = null;
        //1、先根据user.userId查询该用户是否已经绑定过微信
        LocalAuth la = localAuthMapper.selectLocalAuthByUserId(userId);
        if (la != null) {
            //如果查询到就返回Empty，说明已经注册（已经绑定过微信）
            return new O2oExecution<>(CommonStateEnum.EMPTY);
        }
        //2、否则就没有注册，再判断该用户名是否已经有人使用
        LocalAuth localAuthWhere = new LocalAuth();
        localAuthWhere.setUsername(localAuth.getUsername());
        LocalAuth l = localAuthMapper.selectLocalAuthByUserNameAndPassword(localAuthWhere);
        if (l != null) {
            //如果有人使用就返回null
            return null;
        }
        // 3、调用注册方法
        localAuth.setPassword(DigestUtils.md5DigestAsHex(localAuth.getPassword().getBytes()));
        localAuth.setLastEditTime(new Date());
        localAuth.setCreateTime(new Date());
        User user = new User();
        user.setUserId(userId);
        localAuth.setUser(user);
        int effectNum = localAuthMapper.insertLocalAuth(localAuth);
        if (effectNum <= 0) {
            throw new ServiceRuntimeException("绑定微信失败！");
        }
        return new O2oExecution<>(CommonStateEnum.SUCCESS);
    }

    @Override
    public O2oExecution<LocalAuth> modifyLocalAuthPassword(LocalAuth localAuth, String newPassword, Long userId) {
        //通过LocalAuth中的user.userId, username,password，lastEditTime更改密码
        //将旧密码进行md5加密
        newPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        localAuth.setLastEditTime(new Date());
        User user = new User();
        user.setUserId(userId);
        localAuth.setUser(user);
        int effectNum = localAuthMapper.updateLocalAuthPassword(localAuth, newPassword);
        if (effectNum <= 0) {
            throw new ServiceRuntimeException("修改密码失败！");
        }
        return new O2oExecution<>(CommonStateEnum.SUCCESS);
    }
}
