package com.ahead.web.controller;

import com.ahead.dto.ImgWrap;
import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.enums.ShopStateEnum;
import com.ahead.pojo.Area;
import com.ahead.pojo.Shop;
import com.ahead.pojo.ShopCategory;
import com.ahead.pojo.User;
import com.ahead.service.AreaService;
import com.ahead.service.ShopCategoryService;
import com.ahead.service.ShopService;
import com.ahead.util.CodeUtil;
import com.ahead.util.JsonUtil;
import com.ahead.util.ModelMapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/18
 */
@Controller
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private ShopCategoryService shopCategoryService;


    @RequestMapping(value = "/getShopList")
    @ResponseBody
    public Map<String, Object> getShopList(String shopName, Integer page, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        Shop shopWhere = new Shop();
        if (shopName != null && !"".equals(shopName.trim())) {
            shopWhere.setShopName(shopName);
        }

        //从session中获取用户
        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
            modelMap.put("success", false);
            modelMap.put("errorMsg", "用户非法未登录访问！");
            return modelMap;
        }
        shopWhere.setOwner(u);
        try {
            //调用service 根据用户查出属于该用户下的所有店铺
            O2oExecution<Shop> shopO2oExecution = shopService.getShopList(shopWhere, page, 3);
            if (shopO2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
                return ModelMapUtil.errorMsg(shopO2oExecution.getStateInfo(), modelMap);
            }
            modelMap.put("success", true);
            modelMap.put("pageInfo", shopO2oExecution.getPageInfo());
            modelMap.put("user", u);
            return modelMap;
            //出现了内部异常
        } catch (Exception e) {
            return ModelMapUtil.ExceptionMsg(e, modelMap);
        }
    }

    /**
     * 点击修改店铺的回显数据
     *
     * @param shopId
     * @return
     */
    @RequestMapping("/echoShop")
    @ResponseBody
    public Map<String, Object> echoShop(Long shopId) {
        Map<String, Object> modelMap = new HashMap<>();
        if (shopId != null) {
            O2oExecution<Shop> shopO2oExecution = shopService.getShopById(shopId);
            //前端修改页面不需要修改店铺类别，所以这里只要查出区域列表就行
            O2oExecution<Area> areaO2oExecution = areaService.getAreaList();
            if(shopO2oExecution.getState() != CommonStateEnum.SUCCESS.getState()){
                return ModelMapUtil.errorMsg(areaO2oExecution.getStateInfo(), modelMap);
            }
            if(areaO2oExecution.getState() != CommonStateEnum.SUCCESS.getState()){
                return ModelMapUtil.errorMsg(areaO2oExecution.getStateInfo(), modelMap);
            }

            modelMap.put("success", true);
            modelMap.put("areaList", areaO2oExecution.getList());
            modelMap.put("shop", shopO2oExecution.getT());
        } else {
            modelMap.put("success", false);
            modelMap.put("errorMsg", "shopId empty");
        }
        return modelMap;
    }

    @RequestMapping("/modifyShop")
    @ResponseBody
    public Map<String, Object> modifyShop(String shopStr, MultipartFile shopImg, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        Shop shop = JsonUtil.jsonToPojo(shopStr, Shop.class);
        //校验验证码是否正确
        if (!CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errorMsg", "验证码输入错误");
            return modelMap;
        }
        //非空判断
        if (shop.getShopId() == null) {
            modelMap.put("success", false);
            modelMap.put("errorMsg", "非法操作");
            return modelMap;
        }

        O2oExecution<Shop> shopExecution = null;
        if (shopImg == null) {
            //如果上传的图片为空说明不需要修改图片
            shopExecution = shopService.modifyShop(shop, null);
        } else {
            //调用service修改店铺和店铺缩略图
            try {
                ImgWrap imgWrap = new ImgWrap();
                imgWrap.setInputStream(shopImg.getInputStream());
                imgWrap.setName(shopImg.getOriginalFilename());
                shopExecution = shopService.modifyShop(shop, imgWrap);
            } catch (IOException e) {
                return ModelMapUtil.ExceptionMsg(e, modelMap);
            }
        }
        if (shopExecution.getState() == ShopStateEnum.SUCCESS.getState()) {
            modelMap.put("success", true);
        } else {
            return ModelMapUtil.errorMsg(shopExecution.getStateInfo(), modelMap);
        }
        return modelMap;

    }

    @RequestMapping(value = "/getShopInitInfo", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getShopInitInfo() {
        Map<String, Object> modelMap = new HashMap<>();

        O2oExecution<Area> areaO2oExecution = areaService.getAreaList();
        O2oExecution<ShopCategory> shopCategoryO2oExecution = shopCategoryService.getShopCategoryByWhere(new ShopCategory());

        if (shopCategoryO2oExecution.getState() != ShopStateEnum.SUCCESS.getState()) {
            return ModelMapUtil.errorMsg(shopCategoryO2oExecution.getStateInfo(), modelMap);
        }
        if (areaO2oExecution.getState() != ShopStateEnum.SUCCESS.getState()) {
            return ModelMapUtil.errorMsg(areaO2oExecution.getStateInfo(), modelMap);
        }
        modelMap.put("success", true);
        modelMap.put("areaList", areaO2oExecution.getList());
        modelMap.put("shopCategoryList", shopCategoryO2oExecution.getList());

        return modelMap;
    }

    /**
     * 注册店铺
     *
     * @param shopStr
     * @param shopImg
     * @param request
     * @return
     */
    @RequestMapping(value = "/registerShop", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> registerShop(String shopStr, MultipartFile shopImg, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        Shop shop = JsonUtil.jsonToPojo(shopStr, Shop.class);
        //校验验证码是否正确
        if (!CodeUtil.checkVerifyCode(request)) {
            return ModelMapUtil.errorMsg("验证码输入错误", modelMap);
        }
        //非空判断
        if (shop == null) {
            return ModelMapUtil.errorMsg("请填写店铺信息", modelMap);
        }
        if (shopImg == null) {
            return ModelMapUtil.errorMsg("请上传店铺图片", modelMap);
        }
        User owner = (User) request.getSession().getAttribute("user");
        if (owner == null) {
            return ModelMapUtil.errorMsg("用户非法登陆", modelMap);
        }
        shop.setOwner(owner);
        //调用service注册店铺
        O2oExecution<Shop> shopExecution = null;
        try {
            ImgWrap imgWrap = new ImgWrap();
            imgWrap.setInputStream(shopImg.getInputStream());
            imgWrap.setName(shopImg.getOriginalFilename());
            shopExecution = shopService.addShop(shop, imgWrap);
        } catch (IOException e) {
            return ModelMapUtil.ExceptionMsg(e, modelMap);
        }
        if (shopExecution.getState() == ShopStateEnum.CHECK.getState()) {
            modelMap.put("success", true);
            return modelMap;
        } else {
            return ModelMapUtil.errorMsg(shopExecution.getStateInfo(), modelMap);
        }
    }

    /**
     * 下面都是页面的转发
     */
    @RequestMapping(value = "/shopOperationPage")
    public String shopOperation() {
        return "shop/shopoperation";
    }

    @RequestMapping("/shopListPage")
    public String shopList() {
        return "shop/shoplist";
    }

    @RequestMapping("/shopManagePage")
    public String shopManage() {
        return "shop/shopmanage";
    }

}
