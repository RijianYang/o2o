package com.ahead.mapper;

import com.ahead.pojo.Shop;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: Yang
 * @Date: 2019/1/16 16:20
 * @Version 1.0
 */
public interface ShopMapper {

    /**
     * 分页查询，可输入的条件有：店铺名(模糊)，店铺状态，店铺类别，区域Id，owner。
     * @param shopWhere 分页查询的条件
     * @return
     */
    List<Shop> selectShopList(@Param("shopWhere") Shop shopWhere);

    /**
     * 插入一个店铺
     * @param shop
     * @return
     */
    int insertShop(Shop shop);

    /**
     * 修改店铺
     * @param shop
     */
    int updateShop(Shop shop);

    /**
     * 根据店铺id返回店铺完整信息
     * @param shopId
     * @return
     */
    Shop selectShopById(Long shopId);
}
