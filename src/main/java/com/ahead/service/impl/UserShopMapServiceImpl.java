package com.ahead.service.impl;

import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.exceptions.ServiceRuntimeException;
import com.ahead.mapper.UserShopMapMapper;
import com.ahead.pojo.Shop;
import com.ahead.pojo.User;
import com.ahead.pojo.UserShopMap;
import com.ahead.service.UserShopMapService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/19
 */
@Service
public class UserShopMapServiceImpl implements UserShopMapService {

    @Autowired
    private UserShopMapMapper userShopMapMapper;

    @Override
    public O2oExecution<UserShopMap> getUserShopMapList(UserShopMap userShopMapWhere, Integer page, Integer pageSize) {
        O2oExecution<UserShopMap> o2oExecution = null;
        if (page == null || page <= 0) {
            page = 1;
        }
        PageHelper.startPage(page, pageSize);
        PageHelper.startPage(page, pageSize);
        List<UserShopMap> userShopMapList = userShopMapMapper.selectUserShopMapListByWhere(userShopMapWhere);
        if (userShopMapList == null || userShopMapList.size() <= 0) {
            o2oExecution = new O2oExecution<>(CommonStateEnum.EMPTY);
        } else {
            PageInfo<UserShopMap> pageInfo = new PageInfo<>(userShopMapList);
            o2oExecution = new O2oExecution<>(CommonStateEnum.SUCCESS);
            o2oExecution.setPageInfo(pageInfo);
        }
        return o2oExecution;
    }

    @Override
    public O2oExecution<UserShopMap> getUserShopMapById(Long shopId, Long buyerId) {
        O2oExecution<UserShopMap> o2oExecution = null;
        UserShopMap userShopMap = userShopMapMapper.selectUserShopMapById(buyerId, shopId);
        if (userShopMap == null) {
            userShopMap = new UserShopMap();
            userShopMap.setPoint(0);
            Shop shop = new Shop();
            shop.setShopId(shopId);
            User buyer = new User();
            buyer.setUserId(buyerId);
            userShopMap.setShop(shop);
            userShopMap.setBuyer(buyer);
            userShopMap.setCreateTime(new Date());
            userShopMap.setLastEditTime(new Date());
            int effectNum = userShopMapMapper.insertUserShopMap(userShopMap);
            if (effectNum <= 0) {
                throw new ServiceRuntimeException("初始化该店铺会员失败！");
            }
        }
        o2oExecution = new O2oExecution<>(CommonStateEnum.SUCCESS);
        o2oExecution.setT(userShopMap);
        return o2oExecution;
    }

}
