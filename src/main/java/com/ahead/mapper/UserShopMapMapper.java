package com.ahead.mapper;

import com.ahead.pojo.UserShopMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/10
 */
public interface UserShopMapMapper {

    /**
     * 根据条件查询出用户的积分集合<br/>
     * 1、按顾客信息精确查询(buyer.userId)<br/>
     * 2、按店铺信息精确查询(shop.shopId)<br/>
     * 3、按顾客姓名模糊查询(buyer.name)<br/>
     * 4、按店铺名称模糊查询(shop.shopName)<br/>
     * 5、按创建时间范围查询(createTime)
     * @param userShopMapWhere
     * @return
     */
    List<UserShopMap> selectUserShopMapListByWhere(UserShopMap userShopMapWhere);

    /**
     * 根据用户id和店铺id查询该用户在该店铺的总积分
     * @param userId
     * @param shopId
     * @return
     */
    UserShopMap selectUserShopMapById(@Param("userId") Long userId,@Param("shopId") Long shopId);

    /**
     * 添加一条用户店铺的积分记录
     * @param userShopMap
     * @return
     */
    int insertUserShopMap(UserShopMap userShopMap);

    /**
     * 更新用户在某店铺的积分<br/>
     * 条件是user.userId和shop.shopId
     * @param userShopMap
     * @return
     */
    int updateUserShopMapById(UserShopMap userShopMap);
}
