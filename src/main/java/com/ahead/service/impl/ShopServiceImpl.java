package com.ahead.service.impl;

import com.ahead.dto.ImgWrap;
import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.enums.ShopStateEnum;
import com.ahead.exceptions.ServiceRuntimeException;
import com.ahead.mapper.ShopAuthMapMapper;
import com.ahead.mapper.ShopMapper;
import com.ahead.pojo.Shop;
import com.ahead.pojo.ShopAuthMap;
import com.ahead.service.ShopService;
import com.ahead.util.ImageUtil;
import com.ahead.util.PathUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author: Yang
 * @Date: 2019/1/17 9:56
 * @Version 1.0
 */
@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private ShopAuthMapMapper shopAuthMapMapper;

    @Override
    public O2oExecution<Shop> getShopList(Shop shopWhere, Integer page, Integer pageSize) throws ServiceRuntimeException {

        if (page == null || page <= 0) {
            page = 1;
        }
        O2oExecution<Shop> shopO2oExecution = null;

        PageHelper.startPage(page, pageSize);
        List<Shop> shopList = shopMapper.selectShopList(shopWhere);

        if (shopList == null) {
            shopO2oExecution = new O2oExecution<>(CommonStateEnum.EMPTY);
        } else {
            PageInfo<Shop> pageInfo = new PageInfo<>(shopList);
            shopO2oExecution = new O2oExecution<>(CommonStateEnum.SUCCESS);
            shopO2oExecution.setPageInfo(pageInfo);
        }
        return shopO2oExecution;
    }

    /**
     * 程序运行中RuntimeException异常或者该子类异常都会进行事务回滚
     * 而别的异常是不会进行事务回滚的，该提交的还是会提交
     *
     * @param shop    传过来的店铺对象
     * @param imgWrap 封装了上传图片的输入流和名称
     * @return
     */
    @Override
    public O2oExecution<Shop> addShop(Shop shop, ImgWrap imgWrap) throws ServiceRuntimeException {
        //对店铺进行非空判断
        if (shop == null) {
            return new O2oExecution<Shop>(ShopStateEnum.NULL_SHOP);
        } else {
            //1、给店铺信息赋初始值
            shop.setCreateTime(new Date());
            shop.setLastEditTime(new Date());
            //默认是审核中
            shop.setEnableStatus(0);
            //2、保存店铺

            int effectedNum = shopMapper.insertShop(shop);
            if (effectedNum <= 0) {
                throw new ServiceRuntimeException("保存店铺失败！");
            } else {
                if (imgWrap.getInputStream() != null) {

                    //3、保存图片到磁盘中
                    addShopImg(shop, imgWrap);

                    //4、更新数据库中的图片字段
                    effectedNum = shopMapper.updateShop(shop);
                    if (effectedNum <= 0) {
                        throw new ServiceRuntimeException("更新店铺图片失败！");
                    }
                }
            }
            //4、添加完店铺后自动把店家添加到店铺授权表中
            ShopAuthMap shopAuthMap = new ShopAuthMap();
            shopAuthMap.setShop(shop);
            shopAuthMap.setEmployee(shop.getOwner());
            shopAuthMap.setTitleFlag(0);
            shopAuthMap.setLastEditTime(new Date());
            shopAuthMap.setEnableStatus(1);
            shopAuthMap.setCreateTime(new Date());
            shopAuthMap.setTitle("店家");
            shopAuthMapMapper.insertShopAuthMap(shopAuthMap);
        }
        return new O2oExecution<Shop>(ShopStateEnum.CHECK, shop);
    }

    @Override
    public O2oExecution<Shop> getShopById(Long shopId) {
        O2oExecution<Shop> o2oExecution = null;
        Shop shop = shopMapper.selectShopById(shopId);
        //判断返回的对象是否为空，然后进行相应的处理，这里会初始化o2oExecution
        o2oExecution = O2oExecution.isEmpty(o2oExecution, shop);

        return o2oExecution;

    }

    /**
     * 修改店铺信息
     *
     * @param shop
     * @param imgWrap 封装了上传图片的输入流和名称
     * @return
     * @throws ServiceRuntimeException
     */
    @Override
    public O2oExecution<Shop> modifyShop(Shop shop, ImgWrap imgWrap) throws ServiceRuntimeException {
        if (shop == null || shop.getShopId() == null) {
            return new O2oExecution<Shop>(ShopStateEnum.NULL_SHOP);
        } else {
            //1、判断是否需要修改图片
            if (imgWrap != null && imgWrap.getInputStream() != null && imgWrap.getName() != null && !"".equals(imgWrap.getName())) {
                Shop tempShop = shopMapper.selectShopById(shop.getShopId());
                //删除对应的图片
                if (tempShop.getShopImg() != null) {
                    ImageUtil.deleteFileOrPath(tempShop.getShopImg());
                }
                //再把需要修改的图片添加到磁盘中并设置给shop
                //因为addShop中对Shop对象进行了操作，所以传shop而不是tempShop
                addShopImg(shop, imgWrap);
            }
            //2、更新店铺信息
            shop.setLastEditTime(new Date());
            int effectNum = shopMapper.updateShop(shop);
            if (effectNum <= 0) {
               throw new ServiceRuntimeException("修改店铺失败！");
            } else {
                //保险起见，这里再去数据库中把最新的店铺查出来
                shopMapper.selectShopById(shop.getShopId());
                return new O2oExecution<Shop>(ShopStateEnum.SUCCESS, shop);
            }
        }
    }

    /**
     * 把图片存在硬盘中并且把相对地址设置给Shop对象
     *
     * @param imgWrap 封装了上传图片的输入流和名称
     * @param shop
     */
    private void addShopImg(Shop shop, ImgWrap imgWrap) {
        //获得图片的相对目录
        String shopImgAddr = PathUtil.getShopImgPath(shop.getShopId());
        //保存到硬盘并添加水印
        String shopImgPath = ImageUtil.generateThumbnail(imgWrap, shopImgAddr, 200, 200, 0.8f);
        //数据库保存的就是相对地址
        shop.setShopImg(shopImgPath);
    }
}
