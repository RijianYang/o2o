package com.ahead.mapper;

import com.ahead.BaseTest;
import com.ahead.pojo.Shop;
import com.ahead.pojo.User;
import com.ahead.pojo.UserShopMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/10
 */
public class UserShopMapMapperTest extends BaseTest {

    @Autowired
    private UserShopMapMapper userShopMapMapper;

    @Test
    public void testInsertUserShopMap() {
        UserShopMap userShopMap = new UserShopMap();
        userShopMap.setCreateTime(new Date());
        userShopMap.setPoint(47);

        Shop shop = new Shop();
        shop.setShopId(15L);
        userShopMap.setShop(shop);

        User buyer = new User();
        buyer.setUserId(70L);
        userShopMap.setBuyer(buyer);

        userShopMapMapper.insertUserShopMap(userShopMap);
        System.out.println(userShopMap.getUserShopMapId());
    }

    @Test
    public void testSelectUserShopMapListByWhere() throws ParseException {
       UserShopMap userShopMapWhere = new UserShopMap();
       User buyer = new User();
//       buyer.setUserId(70L);
//       userShopMapWhere.setBuyer(buyer);
        Shop shop = new Shop();
        shop.setShopId(15L);
        shop.setShopName("二");
        userShopMapWhere.setShop(shop);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = format.parse("2019-03-10 20:34:35");
        userShopMapWhere.setCreateTime(date);
//        buyer.setName("Y");
//        userShopMapWhere.setBuyer(buyer);
        List<UserShopMap> userShopMaps = userShopMapMapper.selectUserShopMapListByWhere(userShopMapWhere);
        for (UserShopMap userShopMap : userShopMaps) {
            System.out.println(userShopMap);
        }
    }

    @Test
    public void testSelectUserShopMapById() {
        UserShopMap userShopMap = userShopMapMapper.selectUserShopMapById(70L, 15L);
        System.out.println(userShopMap);
    }

    @Test
    public void testUpdateUserShopMapById() {
        UserShopMap userShopMap = new UserShopMap();
        User buyer = new User();
        buyer.setUserId(70L);
        userShopMap.setBuyer(buyer);

        Shop shop = new Shop();
        shop.setShopId(15L);
        userShopMap.setShop(shop);
        userShopMap.setPoint(70);
        userShopMap.setLastEditTime(new Date());
        int effectNum = userShopMapMapper.updateUserShopMapById(userShopMap);
        System.out.println(effectNum);
    }
}
