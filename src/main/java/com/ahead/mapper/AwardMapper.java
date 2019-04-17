package com.ahead.mapper;

import com.ahead.pojo.Award;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/10
 */
public interface AwardMapper {

    /**
     * 根据条件查询所有的奖品信息
     * @param awardWhere
     * @return
     */
    List<Award> selectAwardListByWhere(Award awardWhere);

    /**
     * 根据主键id查询奖品信息<br/>
     * 1、按奖品名称模糊查询（awardName）<br/>
     * 2、按店铺查询（shop.shopId）<br/>
     * 3、按可用状态查询（enableStatus）
     * @param awardId
     * @return
     */
    Award selectAwardById(@Param("awardId") Long awardId);

    /**
     * 插入一条奖品记录
     * @param award
     * @return
     */
    int insertAward(Award award);

    /**
     * 更新奖品信息
     * @param award
     * @return
     */
    int updateAwardById(Award award);

    /**
     * 根据主键id删除一条奖品记录
     * @param awardId
     * @return
     */
    int deleteAwardById(@Param("awardId") Long awardId);
}
