package com.ahead.service;

import com.ahead.dto.O2oExecution;
import com.ahead.pojo.ShopAuthMap;
import com.ahead.pojo.User;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/11
 */
public interface ShopAuthMapService {

    /**
     * 根据店铺id分页查询出该店铺所有的授权信息
     * @param shopId
     * @param page
     * @param pageSize
     * @return
     */
    O2oExecution<ShopAuthMap> getShopAuthMapListByShopId(Long shopId, Integer page, Integer pageSize);

    /**
     * 添加一条授权信息
     * @param shopAuthMap
     * @return
     */
    O2oExecution<ShopAuthMap> addShopAuthMap(ShopAuthMap shopAuthMap, User employee, Long shopId);

    /**
     * 修改title(职称)，enableStatus(授权有效状态)
     * @param shopAuthMap
     * @return
     */
    O2oExecution<ShopAuthMap> modifyShopAuthMap(ShopAuthMap shopAuthMap);

    /**
     * 根据条件获取授权记录
     * @param shopAuthMapWhere
     * @return
     */
    O2oExecution<ShopAuthMap> getShopAuthMapById(ShopAuthMap shopAuthMapWhere);
}
