package com.ahead.service;

import com.ahead.BaseTest;
import com.ahead.dto.O2oExecution;
import com.ahead.pojo.ShopAuthMap;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/11
 */
public class ShopAuthMapServiceTest extends BaseTest {

    @Autowired
    private ShopAuthMapService shopAuthMapService;

    @Test
    public void testGetShopAuthMapListByShopId() {
        O2oExecution<ShopAuthMap> o2oExecution = shopAuthMapService.getShopAuthMapListByShopId(15L, 1, 1);
        PageInfo<ShopAuthMap> pageInfo = o2oExecution.getPageInfo();
        List<ShopAuthMap> list = pageInfo.getList();
        for (ShopAuthMap shopAuthMap : list) {
            System.out.println(shopAuthMap);
        }
        System.out.println("总条数：" + pageInfo.getTotal());
    }
}
