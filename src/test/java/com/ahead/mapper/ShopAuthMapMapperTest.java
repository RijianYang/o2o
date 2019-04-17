package com.ahead.mapper;

import com.ahead.BaseTest;
import com.ahead.pojo.Shop;
import com.ahead.pojo.ShopAuthMap;
import com.ahead.pojo.User;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/10
 */
public class ShopAuthMapMapperTest extends BaseTest {

    @Autowired
    private ShopAuthMapMapper shopAuthMapMapper;

    @Test
    public void testInsertShopAuthMap() {
        ShopAuthMap shopAuthMap = new ShopAuthMap();
        shopAuthMap.setCreateTime(new Date());
        shopAuthMap.setLastEditTime(new Date());

        User employee = new User();
        employee.setUserId(1L);
        shopAuthMap.setEmployee(employee);

        Shop shop = new Shop();
        shop.setShopId(15L);
        shopAuthMap.setShop(shop);

        shopAuthMap.setEnableStatus(1);
        shopAuthMap.setTitle("店员");
        shopAuthMap.setTitleFlag(1);

        shopAuthMapMapper.insertShopAuthMap(shopAuthMap);
        System.out.println(shopAuthMap.getShopAuthMapId());
    }

    @Test
    public void testSelectShopAuthMapListByShopId() {
        List<ShopAuthMap> shopAuthMaps = shopAuthMapMapper.selectShopAuthMapListByShopId(15L);
        for (ShopAuthMap shopAuthMap : shopAuthMaps) {
            System.out.println(shopAuthMap);

        }
    }

    @Test
    public void testSelectShopAuthMapById() {
        ShopAuthMap shopAuthMapWhere = new ShopAuthMap();
        shopAuthMapWhere.setShopAuthMapId(1L);
        ShopAuthMap shopAuthMap = shopAuthMapMapper.selectShopAuthMapById(shopAuthMapWhere);
        System.out.println(shopAuthMap);
    }

    @Test
    public void testUpdateShopAuthMapById() {
        ShopAuthMap shopAuthMap = new ShopAuthMap();
        shopAuthMap.setShopAuthMapId(1L);
        shopAuthMap.setLastEditTime(new Date());
        shopAuthMap.setTitleFlag(2);
        int effectNum = shopAuthMapMapper.updateShopAuthMapById(shopAuthMap);
        System.out.println(effectNum);
    }

    @Test
    public void testDeleteShopAuthMapById() {
        int effectNum = shopAuthMapMapper.deleteShopAuthMap(3L);
        System.out.println(effectNum);

    }
}
