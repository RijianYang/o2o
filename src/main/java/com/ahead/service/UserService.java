package com.ahead.service;

import com.ahead.dto.O2oExecution;
import com.ahead.pojo.User;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/2/25
 */
public interface UserService {

    /**
     * 根据主键获取用户
     * @param id
     * @return
     */
   O2oExecution<User> getUserById(Long id);

    /**
     * 分页查询出所有的账号
     * @param userWhere
     * @param page
     * @param pageSize
     * @return
     */
   O2oExecution<User> getUserList(User userWhere, Integer page, Integer pageSize);

    /**
     * 根据用户id修改用户状态
     * @param user
     * @return
     */
   O2oExecution<User> modifyUserById(User user);
}
