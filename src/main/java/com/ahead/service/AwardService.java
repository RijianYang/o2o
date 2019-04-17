package com.ahead.service;

import com.ahead.dto.ImgWrap;
import com.ahead.dto.O2oExecution;
import com.ahead.exceptions.ServiceRuntimeException;
import com.ahead.pojo.Award;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/19
 */
public interface AwardService {

    /**
     * 下架奖品
     * @param award
     * @return
     */
    O2oExecution<Award> soldOutOrInAward(Award award);

    /**
     * 添加奖品
     * @param award
     * @param imgWrap
     * @return
     */
    O2oExecution<Award> addAward(Award award, ImgWrap imgWrap);

    /**
     * 根据id查询奖品信息
     * @param awardId
     * @return
     */
    O2oExecution<Award> getAwardById(Long awardId);

    /**
     * 修改商品信息(包含缩略图和详情图)
     * @param award
     * @param imgWrap
     * @return
     * @throws ServiceRuntimeException
     */
    O2oExecution<Award> modifyAward(Award award, ImgWrap imgWrap);

    /**
     * 根据奖品名称和店铺id查询出所有的奖品列表
     * @param awardWhere
     * @param page
     * @return
     */
    O2oExecution<Award> getAwardList(Award awardWhere, Integer page, Integer pageSize);
}
