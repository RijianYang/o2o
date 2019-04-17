package com.ahead.service.impl;

import com.ahead.dto.O2oExecution;
import com.ahead.mapper.ShopCategoryMapper;
import com.ahead.pojo.ShopCategory;
import com.ahead.service.ShopCategoryService;
import com.ahead.util.JedisPoolClient;
import com.ahead.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/19
 */
@Service
public class ShopCategoryServiceImpl implements ShopCategoryService {

    private static final String FIRST_LEVEL = "first_level";
    private static final String SECOND_LEVEL = "second_level";
    private static final String H_SECOND_LEVEL = "h_second_level";

    @Autowired
    private JedisPoolClient jedisPoolClient;
    @Autowired
    private ShopCategoryMapper shopCategoryMapper;

    /**
     * 1.该方法中所有一级店铺类别使用Redis中String结构存储<br/>
     * 2.所有的二级店铺使用Redis中String结构存储<br/>
     * 3.某一级类别下有多少个二级店铺使用Hash结构存储<br/>
     *
     * @param shopCategoryWhere
     * @return
     */
    @Override
    public O2oExecution<ShopCategory> getShopCategoryByWhere(ShopCategory shopCategoryWhere) {
        O2oExecution<ShopCategory> o2oExecution = null;
        List<ShopCategory> shopCategoryList = new ArrayList<>();
        String shopCategoryListJson = null;
        //前台传过来的不同的条件从Redis中根据不同的key查询数据
        if (shopCategoryWhere == null) {
            shopCategoryListJson = redisEmpty(FIRST_LEVEL, shopCategoryWhere, shopCategoryList);
        } else if (shopCategoryWhere != null && shopCategoryWhere.getParent() == null) {
            shopCategoryListJson = shopCategoryListJson = redisEmpty(SECOND_LEVEL, shopCategoryWhere, shopCategoryList);
        } else if (shopCategoryWhere != null && shopCategoryWhere.getParent() != null && shopCategoryWhere.getParent().getShopCategoryId() != null) {
            shopCategoryListJson = redisEmpty(H_SECOND_LEVEL, shopCategoryWhere.getParent().getShopCategoryId() + "_" + SECOND_LEVEL, shopCategoryWhere, shopCategoryList);
        }

        //如果非空就把从Redis中查询到Json字符串转换成List
        if (!StringUtils.isEmpty(shopCategoryListJson)) {
            shopCategoryList = JsonUtil.jsonToList(shopCategoryListJson, ShopCategory.class);
        }
        //判断返回的集合是否为空，然后进行相应的处理，这里会初始化o2oExecution
        o2oExecution = O2oExecution.isEmpty(o2oExecution, shopCategoryList);

        return o2oExecution;
    }

    /**
     * Redis中String结构操作<br/>
     * 从Redis中根据key查数据，如果为空的话就从数据库中查询出数据，再把数据存入Redis中，返回null<br/>
     * 如果非空 返回从Redis中查到的Json字符串
     *
     * @param key
     * @param shopCategoryWhere
     * @param shopCategoryList
     * @return
     */
    private String redisEmpty(String key, ShopCategory shopCategoryWhere, List<ShopCategory> shopCategoryList) {
        //从Redis中根据key查数据
        String json = jedisPoolClient.get(key);
        if (StringUtils.isEmpty(json)) {
            //如果为空的话就从数据库中查询出数据
            List<ShopCategory> tempList = shopCategoryMapper.getShopCategoryListByWhere(shopCategoryWhere);
            for (ShopCategory shopCategory : tempList) {
                shopCategoryList.add(shopCategory);
            }
            //再把数据存入Redis中
            jedisPoolClient.set(key, JsonUtil.objectToJson(shopCategoryList));
            return null;
        }
        //如果非空就返回从Redis中查到的Json字符串
        return json;
    }

    /**
     * Redis中Hash结构操作<br/>
     * 从Redis中根据key查数据，如果为空的话就从数据库中查询出数据，再把数据存入Redis中，返回null <br/>
     * 如果非空 返回从Redis中查到的Json字符串
     *
     * @param key
     * @param field
     * @param shopCategoryWhere
     * @param shopCategoryList
     * @return
     */
    private String redisEmpty(String key, String field, ShopCategory shopCategoryWhere, List<ShopCategory> shopCategoryList) {
        //从Redis中根据key和field查数据
        String json = jedisPoolClient.hget(key, field);
        if (StringUtils.isEmpty(json)) {
            //如果为空的话就从数据库中查询出数据
            List<ShopCategory> tempList = shopCategoryMapper.getShopCategoryListByWhere(shopCategoryWhere);
            for (ShopCategory shopCategory : tempList) {
                shopCategoryList.add(shopCategory);
            }
            //再把数据存入Redis中
            jedisPoolClient.hset(key, field, JsonUtil.objectToJson(shopCategoryList));
            return null;
        }
        //如果非空就返回从Redis中查到的Json字符串
        return json;
    }
}
