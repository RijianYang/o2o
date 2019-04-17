package com.ahead.service;

import com.ahead.dto.O2oExecution;
import com.ahead.pojo.UserShopMap;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/19
 */
public interface UserShopMapService {

    /**
     * 根据条件查询用户在每个店铺中的积分列表
     * @param page
     * @param pageSize
     * @return
     */
    O2oExecution<UserShopMap> getUserShopMapList(UserShopMap userShopMapWhere,
                                                 Integer page, Integer pageSize);

    /**
     * 根据店铺和顾客id查出该顾客在该店铺的积分,如果没有就初始化一个并返回
     * @param shopId
     * @param buyerId
     * @return
     */
    O2oExecution<UserShopMap> getUserShopMapById(Long shopId, Long buyerId);

    /**
     * 根据顾客id和店铺id找出对应的顾客总积分，然后修改其积分
     * @param shopId
     * @param buyer
     * @param point
     * @return
     */
//    O2oExecution<UserShopMap> updateUserShopMapById(Long shopId, Long buyer, Integer point);
}
