package com.ahead.service;

import com.ahead.dto.O2oExecution;
import com.ahead.pojo.LocalAuth;
import org.apache.ibatis.annotations.Param;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/2
 */
public interface LocalAuthService {

    /**
     * 根据用户名密码校验登陆
     * @param localAuth
     * @return
     */
    O2oExecution<LocalAuth> findLocalAuthByUserNameAndPassword(LocalAuth localAuth);

    /**
     * 根据用户id查询平台账号
     * @param userId
     * @return
     */
    O2oExecution<LocalAuth> findLocalAuthByUserId(Long userId);

    /**
     * 注册平台账号(绑定微信，生成平台专属的账号)
     * @param localAuth
     * @param userId
     * @return
     */
    O2oExecution<LocalAuth> addLocalAuth(LocalAuth localAuth, Long userId);

    /**
     * 通过userId, username,password，lastEditTime更改密码
     * @param localAuth
     * @param newPassword
     * @param userId
     * @return
     */
    O2oExecution<LocalAuth> modifyLocalAuthPassword(LocalAuth localAuth, String newPassword, Long userId);
}
