package com.ahead.mapper;

import com.ahead.pojo.ShopAuthMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/10
 */
public interface ShopAuthMapMapper {

    /**
     * 通过shopId查询出该店铺的所有授权信息
     * @param shopId
     * @return
     */
    List<ShopAuthMap> selectShopAuthMapListByShopId(@Param("shopId") Long shopId);

    /**
     * 新增一条店铺与店员的授权关系
     * @param shopAuthMap
     * @return
     */
    int insertShopAuthMap(ShopAuthMap shopAuthMap);

    /**
     * 更新授权信息<br/>
     * 1、title(职称)<br/>
     * 2、titleFlag(职称符号[可用于权限控制])<br/>
     * 3、enableStatus(可用状态)<br/>
     * 4、lastEditTime(最后一次修改时间)
     * @param shopAuthMap
     * @return
     */
    int updateShopAuthMapById(ShopAuthMap shopAuthMap);

    /**
     * 对某员工除权
     * @param shopAuthMapId
     * @return
     */
    int deleteShopAuthMap(@Param("shopAuthMapId") Long shopAuthMapId);

    /**
     * 查询员工授权信息
     * @param shopAuthMap
     * @return
     */
    ShopAuthMap selectShopAuthMapById(@Param("shopAuthMap") ShopAuthMap shopAuthMap);


}
