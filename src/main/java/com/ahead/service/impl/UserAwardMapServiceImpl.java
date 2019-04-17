package com.ahead.service.impl;

import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.exceptions.ServiceRuntimeException;
import com.ahead.mapper.AwardMapper;
import com.ahead.mapper.ShopMapper;
import com.ahead.mapper.UserAwardMapMapper;
import com.ahead.mapper.UserShopMapMapper;
import com.ahead.pojo.*;
import com.ahead.service.UserAwardMapService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/20
 */
@Service
public class UserAwardMapServiceImpl implements UserAwardMapService {

    @Autowired
    private UserAwardMapMapper userAwardMapMapper;
    @Autowired
    private UserShopMapMapper userShopMapMapper;
    @Autowired
    private AwardMapper awardMapper;
    @Autowired
    private ShopMapper shopMapper;

    @Override
    public O2oExecution<UserAwardMap> getUserAwardMapList(UserAwardMap userAwardMapWhere, Integer page, Integer pageSize) {
        O2oExecution<UserAwardMap> o2oExecution = null;

        if (page == null || page <= 0) {
            page = 1;
        }
        PageHelper.startPage(page, pageSize);
        List<UserAwardMap> userAwardMapList = userAwardMapMapper.selectUserAwardMapListByWhere(userAwardMapWhere);
        if (userAwardMapList == null || userAwardMapList.size() <= 0) {
            o2oExecution = new O2oExecution<>(CommonStateEnum.EMPTY);
        } else {
            PageInfo<UserAwardMap> pageInfo = new PageInfo<>(userAwardMapList);
            o2oExecution = new O2oExecution<>(CommonStateEnum.SUCCESS);
            o2oExecution.setPageInfo(pageInfo);
        }
        return o2oExecution;
    }

    @Override
    public O2oExecution<UserAwardMap> saveUserAwardMap(Long buyerId, Long awardId, Long shopId) {
        //1、先查出该用户在该店铺下的总积分
        UserShopMap userShopMap = userShopMapMapper.selectUserShopMapById(buyerId, shopId);
        //2、查出该奖品需要的积分，如果大于总积分，抛出异常信息积分不足
        Award award = awardMapper.selectAwardById(awardId);
        if (userShopMap.getPoint() < award.getPoint()) {
            throw new ServiceRuntimeException("积分不足！");
        }
        //3、插入用户兑换奖品记录
        UserAwardMap userAwardMap = new UserAwardMap();
        User buyer = new User();
        buyer.setUserId(buyerId);
        userAwardMap.setBuyer(buyer);

        Award a = new Award();
        a.setAwardId(awardId);
        userAwardMap.setAward(a);

        Shop shop = new Shop();
        shop.setShopId(shopId);
        userAwardMap.setShop(shop);
        userAwardMap.setUsedStatus(0);
        userAwardMap.setCreateTime(new Date());

        //4、默认操作员设置为该店铺的店家(不设置默认操作员，操作员为null，兑换记录中多表查询就查不到该条记录)
        Shop s = shopMapper.selectShopById(shopId);
        userAwardMap.setOperator(s.getOwner());

        int effectNum = userAwardMapMapper.insertUserAwardMap(userAwardMap);
        if (effectNum <= 0) {
            throw new ServiceRuntimeException("添加用户兑换奖品失败！");
        }
        //5、减少该用户在该店铺中的积分
        UserShopMap userShopMapWhere = new UserShopMap();
        userShopMapWhere.setPoint(userShopMap.getPoint() - award.getPoint());
        Shop shopWhere = new Shop();
        shopWhere.setShopId(shopId);
        userShopMapWhere.setShop(shopWhere);
        User buyerWhere = new User();
        buyerWhere.setUserId(buyerId);
        userShopMapWhere.setBuyer(buyerWhere);
        userShopMapWhere.setLastEditTime(new Date());
        int en = userShopMapMapper.updateUserShopMapById(userShopMapWhere);
        if (en <= 0) {
            throw new ServiceRuntimeException("减少用户总积分失败！");
        }

        return new O2oExecution<>(CommonStateEnum.SUCCESS);
    }

    @Override
    public O2oExecution<UserAwardMap> getUserAwardMapById(Long userAwardMapId) {
        O2oExecution<UserAwardMap> o2oExecution = null;
        UserAwardMap userAwardMap = userAwardMapMapper.selectUserAwardMapById(userAwardMapId);
        o2oExecution = O2oExecution.isEmpty(o2oExecution, userAwardMap);
        return o2oExecution;
    }

    @Override
    public O2oExecution<UserAwardMap> updateUserAwardMapById(Long userAwardMapId, User operator) {
        O2oExecution<UserAwardMap> o2oExecution = null;
        UserAwardMap userAwardMap = userAwardMapMapper.selectUserAwardMapById(userAwardMapId);
        //如果为1说明已经领取过该奖品
        if (userAwardMap.getUsedStatus() == 1) {
            o2oExecution = new O2oExecution<>(CommonStateEnum.EMPTY);
        } else {
            //否则就修改其状态为1（已领取）并修改其默认操作员
            userAwardMap.setUsedStatus(1);
            userAwardMap.setOperator(operator);
            userAwardMapMapper.updateUserAwardMapById(userAwardMap);
            o2oExecution = new O2oExecution<>(CommonStateEnum.SUCCESS);
        }
        return o2oExecution;
    }
}
