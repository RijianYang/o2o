package com.ahead.service.impl;

import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.exceptions.ServiceRuntimeException;
import com.ahead.mapper.UserMapper;
import com.ahead.pojo.User;
import com.ahead.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/2/25
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public O2oExecution<User> getUserById(Long id) {
        O2oExecution<User> o2oExecution = null;
        User user = userMapper.selectUserById(id);
        //判断获取过来的对象是否为空，然后进行相应的处理，这里面会初始化O2oExecution
        o2oExecution = O2oExecution.isEmpty(o2oExecution, user);
        return o2oExecution;
    }

    @Override
    public O2oExecution<User> getUserList(User userWhere, Integer page, Integer pageSize) {
        if (page == null || page <= 0) {
            page = 1;
        }
        O2oExecution<User> o2oExecution = null;
        PageHelper.startPage(page, pageSize);
        List<User> users = userMapper.selectUserListByWhere(userWhere);
        if (users == null || users.size() == 0) {
            o2oExecution = new O2oExecution<>(CommonStateEnum.EMPTY);
        } else {
            PageInfo<User> pageInfo = new PageInfo<>(users);
            o2oExecution = new O2oExecution<>(CommonStateEnum.SUCCESS);
            o2oExecution.setPageInfo(pageInfo);
        }
        return o2oExecution;
    }

    @Override
    public O2oExecution<User> modifyUserById(User user) {
        int effectNum = userMapper.updateUserById(user);
        if (effectNum <= 0) {
            throw new ServiceRuntimeException("更新用户失败！");
        }
        return new O2oExecution<>(CommonStateEnum.SUCCESS);
    }
}
