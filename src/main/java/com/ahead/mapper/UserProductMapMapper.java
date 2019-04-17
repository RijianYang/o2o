package com.ahead.mapper;

import com.ahead.pojo.UserProductMap;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/10
 */
public interface UserProductMapMapper {

    /**
     * 根据条件查询用户消费商品的记录集合<br/>
     * 1、按顾客信息精确查询(buyer.userId)<br/>
     * 2、按店铺信息精确查询(shop.shopId)<br/>
     * 3、按顾客名字模糊查询(buyer.name)<br/>
     * 4、按商品名字模糊查询(product.productName)<br/>
     * 5、按消费日期范围查询:查询该日期之后所有的商品消费记录(createTime)
     * @param userProductMapWhere
     * @return
     */
    List<UserProductMap> selectUserProductMapListByWhere(UserProductMap userProductMapWhere);

    /**
     * 添加一条商品消费记录
     * @param userProductMap
     * @return
     */
    int insertUserProductMap(UserProductMap userProductMap);
}
