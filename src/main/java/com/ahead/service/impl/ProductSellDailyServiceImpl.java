package com.ahead.service.impl;

import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.mapper.ProductSellDailyMapper;
import com.ahead.pojo.ProductSellDaily;
import com.ahead.pojo.Shop;
import com.ahead.service.ProductSellDailyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/16
 */
@Service
public class ProductSellDailyServiceImpl implements ProductSellDailyService {

    @Autowired
    private ProductSellDailyMapper productSellDailyMapper;

    Logger logger = LoggerFactory.getLogger(ProductSellDailyServiceImpl.class);

    @Override
    public O2oExecution<ProductSellDaily> dailyCalculate() {
        logger.info("Quartz is running。");
        //插入前一天有销量的商品
        int effectNum = productSellDailyMapper.insertProductSellDaily();
        //这里不能判断返回影响的行数是否为0，因为有可能前一天的销量为0
        //插入前一天有销量的商品数据后再插入没有销量的商品，使其销量为0，这样前台友好显示
        int eNum= productSellDailyMapper.insertDefaultProductSellDaily();
        //这里不用判断，因为有可能都有销量
        return new O2oExecution<>(CommonStateEnum.SUCCESS);
    }

    @Override
    public O2oExecution<ProductSellDaily> getProductSellDailyListByShopId(Long shopId, Date beginTime, Date endTime) {
        O2oExecution<ProductSellDaily> o2oExecution = null;
        ProductSellDaily productSellDailyWhere = new ProductSellDaily();
        Shop shop = new Shop();
        shop.setShopId(shopId);
        productSellDailyWhere.setShop(shop);
        List<ProductSellDaily> productSellDailyList = productSellDailyMapper.selectProductSellDailyListByWhere(productSellDailyWhere, beginTime, endTime);
        o2oExecution = O2oExecution.isEmpty(o2oExecution, productSellDailyList);
        return o2oExecution;
    }

}
