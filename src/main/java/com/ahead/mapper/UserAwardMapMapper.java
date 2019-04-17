package com.ahead.mapper;

import com.ahead.pojo.UserAwardMap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/10
 */
public interface UserAwardMapMapper {

    /**
     * 根据条件查询出用户已领取的奖品映射集合<br/>
     * 1、按顾客信息精确查询(主键buyer.userId)<br/>
     * 2、按某个店铺精确查询(主键shop.shopId)<br/>
     * 3、按奖品名称模糊查询(award.awardName)<br/>
     * 4、按顾客名字模糊查询(buyer.name)<br/>
     * 5、按奖品使用状态查询(usedStatus)
     * @param userAwardMapWhere
     * @return
     */
    List<UserAwardMap> selectUserAwardMapListByWhere(UserAwardMap userAwardMapWhere);

    /**
     * 根据主键id查询某一条用户已兑换的奖品信息
     * @param userAwardMapId
     * @return
     */
    UserAwardMap selectUserAwardMapById(@Param("userAwardMapId") Long userAwardMapId);

    /**
     * 添加一条奖品兑换信息
     * @param userAwardMap
     * @return
     */
    int insertUserAwardMap(UserAwardMap userAwardMap);

    /**
     * 根据主键id更新奖品领取状态
     * @param userAwardMap
     * @return
     */
    int updateUserAwardMapById(UserAwardMap userAwardMap);
}
