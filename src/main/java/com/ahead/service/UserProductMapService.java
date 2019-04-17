package com.ahead.service;

import com.ahead.dto.O2oExecution;
import com.ahead.pojo.UserProductMap;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/18
 */
public interface UserProductMapService {

    /**
     * 根据顾条件查询出店铺的分页商品消费信息
     * @return
     */
    O2oExecution<UserProductMap> getUserProductListByWhere(UserProductMap userProductMapWhere,
                                                           Integer page, Integer pageSize);

    /**
     * 为该顾客添加一条消费商品记录，并且增加对应顾客的总积分
     * @param buyerId
     * @param productId
     * @param shopId
     * @return
     */
    O2oExecution<UserProductMap> addUserProductMap(Long buyerId, Long productId, Long shopId, Long operatorId);
}
