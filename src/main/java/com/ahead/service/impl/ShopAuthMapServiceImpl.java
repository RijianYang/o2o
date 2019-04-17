package com.ahead.service.impl;

import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.exceptions.ServiceRuntimeException;
import com.ahead.mapper.ShopAuthMapMapper;
import com.ahead.pojo.Shop;
import com.ahead.pojo.ShopAuthMap;
import com.ahead.pojo.User;
import com.ahead.service.ShopAuthMapService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/11
 */
@Service
public class ShopAuthMapServiceImpl implements ShopAuthMapService {

    @Autowired
    private ShopAuthMapMapper shopAuthMapMapper;

    @Override
    public O2oExecution<ShopAuthMap> getShopAuthMapListByShopId(Long shopId, Integer page, Integer pageSize) {
        if (page == null || page <= 0) {
            page = 1;
        }
        O2oExecution<ShopAuthMap> o2oExecution = null;
        PageHelper.startPage(page, pageSize);
        List<ShopAuthMap> shopAuthMapList = shopAuthMapMapper.selectShopAuthMapListByShopId(shopId);
        if (shopAuthMapList == null || shopAuthMapList.size() <= 0) {
            o2oExecution = new O2oExecution<>(CommonStateEnum.EMPTY);
        } else {
            PageInfo<ShopAuthMap> pageInfo = new PageInfo<>(shopAuthMapList);
            o2oExecution = new O2oExecution<>(CommonStateEnum.SUCCESS);
            o2oExecution.setPageInfo(pageInfo);
        }

        return o2oExecution;
    }

    @Override
    public O2oExecution<ShopAuthMap> addShopAuthMap(ShopAuthMap shopAuthMap, User employee, Long shopId) {
        //先根据employeeId查询授权信息看看有没有已经授权过
        ShopAuthMap shopAuthMapWhere = new ShopAuthMap();
        Shop shopWhere = new Shop();
        shopWhere.setShopId(shopId);
        shopAuthMapWhere.setShop(shopWhere);
        shopAuthMapWhere.setEmployee(employee);
        ShopAuthMap s = shopAuthMapMapper.selectShopAuthMapById(shopAuthMapWhere);
        if (s == null) {
            shopAuthMap.setCreateTime(new Date());
            shopAuthMap.setLastEditTime(new Date());
            shopAuthMap.setEnableStatus(1);
            shopAuthMap.setEmployee(employee);
            Shop shop = new Shop();
            shop.setShopId(shopId);
            shopAuthMap.setShop(shop);
            int effectNum = shopAuthMapMapper.insertShopAuthMap(shopAuthMap);
            if (effectNum <= 0) {
                throw new ServiceRuntimeException("添加授权信息失败！");
            }
            return new O2oExecution<>(CommonStateEnum.SUCCESS);
        } else {
            return new O2oExecution<>(CommonStateEnum.EMPTY);
        }
    }

    @Override
    public O2oExecution<ShopAuthMap> modifyShopAuthMap(ShopAuthMap shopAuthMap) {
        shopAuthMap.setLastEditTime(new Date());
        int effectNum = shopAuthMapMapper.updateShopAuthMapById(shopAuthMap);
        if (effectNum <= 0) {
            throw new ServiceRuntimeException("修改授权信息失败！");
        } else {
            return new O2oExecution<>(CommonStateEnum.SUCCESS);
        }
    }

    @Override
    public O2oExecution<ShopAuthMap> getShopAuthMapById(ShopAuthMap shopAuthMapWhere) {
        O2oExecution<ShopAuthMap> o2oExecution = null;

        ShopAuthMap shopAuthMap = shopAuthMapMapper.selectShopAuthMapById(shopAuthMapWhere);
        //这个方法根据返回的pojo初始化不同的O2oExecution
         o2oExecution = O2oExecution.isEmpty(o2oExecution, shopAuthMap);
        return o2oExecution;
    }

}
