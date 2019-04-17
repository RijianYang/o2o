package com.ahead.service;

import com.ahead.dto.O2oExecution;
import com.ahead.pojo.User;
import com.ahead.pojo.UserAwardMap;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/20
 */
public interface UserAwardMapService {

    /**
     * 根据条件分页查出用户兑换的奖品列表
     * @param userAwardMapWhere
     * @param page
     * @param pageSize
     * @return
     */
    O2oExecution<UserAwardMap> getUserAwardMapList(UserAwardMap userAwardMapWhere, Integer page,
                                                   Integer pageSize);

    /**
     * 插入一条兑换奖品的记录（未兑换）
     * @param buyerId
     * @param awardId
     * @param shopId
     * @return
     */
    O2oExecution<UserAwardMap> saveUserAwardMap(Long buyerId, Long awardId, Long shopId);

    /**
     * 根据主键获取用户奖品映射信息
     * @param userAwardMapId
     * @return
     */
    O2oExecution<UserAwardMap> getUserAwardMapById(Long userAwardMapId);

    /**
     * 根据主键修改用户奖品状态
     * @param userAwardMapId
     * @return
     */
    O2oExecution<UserAwardMap> updateUserAwardMapById(Long userAwardMapId, User operator);
}
