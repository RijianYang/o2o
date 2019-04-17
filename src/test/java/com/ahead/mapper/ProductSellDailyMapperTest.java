package com.ahead.mapper;

import com.ahead.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/10
 */
public class ProductSellDailyMapperTest extends BaseTest {
    @Autowired
    private ProductSellDailyMapper productSellDailyMapper;

    @Test
    public void testInsertProductSellDaily() {
        int effectNum = productSellDailyMapper.insertProductSellDaily();
        System.out.println(effectNum);
    }

    @Test
    public void testInsertDefaultProductSellDaily() {
        int effectNum = productSellDailyMapper.insertDefaultProductSellDaily();
        System.out.println(effectNum);
    }

//    @Test
//    public void testSelectProductSellDailyListByWhere() {
//        Shop shop = new Shop();
//        shop.setShopId(20L);
//        ProductSellDaily productSellDaily = new ProductSellDaily();
//        productSellDaily.setShop(shop);
//        productSellDailyMapper.selectProductSellDailyListByWhere(productSellDaily, );
//    }
}
