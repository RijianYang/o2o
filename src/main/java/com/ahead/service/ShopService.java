package com.ahead.service;

import com.ahead.dto.ImgWrap;
import com.ahead.dto.O2oExecution;
import com.ahead.exceptions.ServiceRuntimeException;
import com.ahead.pojo.Shop;

/**
 * @Author: Yang
 * @Date: 2019/1/17 9:55
 * @Version 1.0
 */
public interface ShopService {

    /**
     * 根据条件分页查询出店铺列表
     * @param shopWhere
     * @param page
     * @param pageSize
     * @return
     */
    O2oExecution<Shop> getShopList(Shop shopWhere, Integer page, Integer pageSize) throws ServiceRuntimeException;

    /**
     * 添加店铺
     * @param shop 传过来的店铺对象
     * @param imgWrap 封装了上传图片的输入流和名称
     * @return
     */
    O2oExecution<Shop> addShop(Shop shop, ImgWrap imgWrap) throws ServiceRuntimeException;

    /**
     * 根据id获取店铺
     * @param shopId
     * @return
     */
    O2oExecution<Shop> getShopById(Long shopId);

    /**
     * 修改店铺信息(包括修改图片)
     * @param shop
     * @param imgWrap 封装了上传图片的输入流和名称
     * @return
     */
    O2oExecution<Shop> modifyShop(Shop shop, ImgWrap imgWrap) throws ServiceRuntimeException;
}
