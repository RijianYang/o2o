package com.ahead.service.impl;

import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.mapper.ProductMapper;
import com.ahead.mapper.UserProductMapMapper;
import com.ahead.mapper.UserShopMapMapper;
import com.ahead.pojo.*;
import com.ahead.service.UserProductMapService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/18
 */
@Service
public class UserProductMapServiceImpl implements UserProductMapService {

    @Autowired
    private UserProductMapMapper userProductMapMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
     private UserShopMapMapper userShopMapMapper;

    @Override
    public O2oExecution<UserProductMap> getUserProductListByWhere(UserProductMap userProductMapWhere, Integer page, Integer pageSize) {
        O2oExecution<UserProductMap> o2oExecution = null;
        if (page == null || page <= 0) {
            page = 1;
        }
        PageHelper.startPage(page, pageSize);
        List<UserProductMap> userProductMapList = userProductMapMapper.selectUserProductMapListByWhere(userProductMapWhere);
        if (userProductMapList == null || userProductMapList.size() <= 0) {
            o2oExecution = new O2oExecution<>(CommonStateEnum.EMPTY);
        } else {
            PageInfo<UserProductMap> pageInfo = new PageInfo<>(userProductMapList);
            o2oExecution = new O2oExecution<>(CommonStateEnum.SUCCESS);
            o2oExecution.setPageInfo(pageInfo);
        }

        return o2oExecution;
    }

    @Override
    public O2oExecution<UserProductMap> addUserProductMap(Long buyerId, Long productId, Long shopId, Long operatorId) {
        //1、先添加对应的用户消费记录
        UserProductMap userProductMap = new UserProductMap();
        User buyer = new User();
        buyer.setUserId(buyerId);
        Product product = new Product();
        product.setProductId(productId);
        Shop shop = new Shop();
        shop.setShopId(shopId);
        User operator = new User();
        operator.setUserId(operatorId);

        userProductMap.setOperator(operator);
        userProductMap.setShop(shop);
        userProductMap.setProduct(product);
        userProductMap.setBuyer(buyer);
        userProductMap.setCreateTime(new Date());
        int effectNum = userProductMapMapper.insertUserProductMap(userProductMap);
        if (effectNum <= 0) {
            //为了让扫码友好显示这里返回EMPTY
            return new O2oExecution<>(CommonStateEnum.EMPTY);
        }
        //2、获取对应商品的积分
        Product p = productMapper.selectProductById(productId);
        //3、再根据shopId和buyerId查询是否为该店铺的会员，如果不是，直接初始化一条数据，如果不是就增加总积分
        UserShopMap userShopMap = userShopMapMapper.selectUserShopMapById(buyerId, shopId);
        if (userShopMap == null) {
            userShopMap = new UserShopMap();
            userShopMap.setPoint(p.getPoint());
            userShopMap.setBuyer(buyer);
            userShopMap.setShop(shop);
            userShopMap.setCreateTime(new Date());
            int en = userShopMapMapper.insertUserShopMap(userShopMap);
            if (en <= 0) {
                //为了让扫码友好显示这里返回EMPTY
                return new O2oExecution<>(CommonStateEnum.EMPTY);
            }
        } else {
            userShopMap.setPoint(userShopMap.getPoint() + p.getPoint());
            userShopMap.setLastEditTime(new Date());
            int en2 = userShopMapMapper.updateUserShopMapById(userShopMap);
            if (en2 <= 0) {
                //为了让扫码友好显示这里返回EMPTY
                return new O2oExecution<>(CommonStateEnum.EMPTY);
            }
        }
        //如果上面都避免了就返回成功
        return new O2oExecution<>(CommonStateEnum.SUCCESS);
    }
}
