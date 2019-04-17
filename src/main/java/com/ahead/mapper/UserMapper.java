package com.ahead.mapper;

import com.ahead.pojo.User;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/2/25
 */
public interface UserMapper {

    /**
     * 保存用户
     * @param user
     * @return
     */
    int insertUser(User user);

    /**
     * 根据用户主键查询用户
     * @param id
     * @return
     */
    User selectUserById(Long id);

    /**
     * 根据主键修改用户类型
     * @param user
     * @return
     */
    int updateUserById(User user);

    /**
     * 根据条件查询出所有的用户
     * @param userWhere
     * @return
     */
    List<User> selectUserListByWhere(User userWhere);
}
