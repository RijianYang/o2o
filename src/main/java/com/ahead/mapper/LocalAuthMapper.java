package com.ahead.mapper;

import com.ahead.pojo.LocalAuth;
import org.apache.ibatis.annotations.Param;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/1
 */
public interface LocalAuthMapper {

    /**
     * 通过用户名和密码查询平台账号信息  登录用
     *
     * @param localAuth
     * @return
     */
    LocalAuth selectLocalAuthByUserNameAndPassword(LocalAuth localAuth);

    /**
     * 通过用户Id查询平台账号
     *
     * @param userId
     * @return
     */
    LocalAuth selectLocalAuthByUserId(Long userId);

    /**
     * 添加平台账号
     *
     * @param localAuth
     * @return
     */
    int insertLocalAuth(LocalAuth localAuth);

    /**
     * 通过LocalAuth中的user.userId, username,password，lastEditTime更改密码
     * @param localAuth
     * @param newPassword
     * @return
     */
    int updateLocalAuthPassword(@Param("localAuth") LocalAuth localAuth, @Param("newPassword") String newPassword);
}
