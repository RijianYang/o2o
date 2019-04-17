package com.ahead.web.controller;

import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.pojo.*;
import com.ahead.service.*;
import com.ahead.util.ModelMapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/29
 */
@Controller
@ControllerAdvice
@RequestMapping("/frontEnd")
public class FrontEndController {

    @Autowired
    private AreaService areaService;
    @Autowired
    private ShopCategoryService shopCategoryService;
    @Autowired
    private HeadLineService headLineService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private UserShopMapService userShopMapService;

    @RequestMapping("/getAwardList")
    @ResponseBody
    private Map<String, Object> getAwardList(Long shopId, String awardName, HttpServletRequest request, Integer page) {
        Map<String, Object> modelMap = new HashMap<>();
        Award awardWhere = new Award();
        Shop shop = new Shop();
        shop.setShopId(shopId);

        awardWhere.setShop(shop);
        awardWhere.setAwardName(awardName);
        awardWhere.setEnableStatus(1);
        O2oExecution<Award> awardO2oExecution = awardService.getAwardList(awardWhere, page, 4);
        User user = (User) request.getSession().getAttribute("user");
        O2oExecution<UserShopMap> usmO2oExecution = userShopMapService.getUserShopMapById(shopId, user.getUserId());
        if (awardO2oExecution.getState() == CommonStateEnum.EMPTY.getState() || usmO2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
            modelMap.put("success", true);
            modelMap.put("pageInfo", null);
            modelMap.put("user", user);
        } else {
            modelMap.put("success", true);
            modelMap.put("user", user);
            modelMap.put("userShopMap", usmO2oExecution.getT());
            modelMap.put("pageInfo", awardO2oExecution.getPageInfo());
        }
        return modelMap;
    }


    /**
     * 如果没有带parentId说明是要查所有的一级店铺类别和区域list
     * 如果带了parentId说明是查该一级类别下的所有二级类别和区域list
     * @return 返回店铺类别list和区域list
     */
    @RequestMapping("/showShopInfo")
    @ResponseBody
    public Map<String, Object> showShopInfo(Long parentId, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        O2oExecution<ShopCategory> shopCategoryO2oExecution = null;
        if(parentId == null) {
            //查出所有的一级类别list
            shopCategoryO2oExecution = shopCategoryService.getShopCategoryByWhere(null);
        } else {
            //查出该一级类别下所有的二级类别list
            ShopCategory shopCategoryWhere = new ShopCategory();
            ShopCategory parent = new ShopCategory();
            parent.setShopCategoryId(parentId);
            shopCategoryWhere.setParent(parent);
            shopCategoryO2oExecution = shopCategoryService.getShopCategoryByWhere(shopCategoryWhere);
        }
        O2oExecution<Area> areaO2oExecution = areaService.getAreaList();
        if(shopCategoryO2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
            return ModelMapUtil.errorMsg(shopCategoryO2oExecution.getStateInfo(), modelMap);
        }
        if(areaO2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
            return ModelMapUtil.errorMsg(areaO2oExecution.getStateInfo(), modelMap);
        }
        User user = (User) request.getSession().getAttribute("user");
        modelMap.put("shopCategoryList", shopCategoryO2oExecution.getList());
        modelMap.put("areaList", areaO2oExecution.getList());
        modelMap.put("user", user);
        modelMap.put("success", true);
        return modelMap;
    }

    /**
     * 根据条件查询对应的店铺列表
     * 条件：shopName, parentId, areaId, shopCategroyId
     * @return
     */
    @RequestMapping("/showFrontEndShop")
    @ResponseBody
    public Map<String, Object> showFrontEndShop(Shop shopWhere, Integer page, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        //这里的条件会在dao层中SQL映射文件中动态拼装出来，所以不需要在java代码中动态拼装
        //前台显示的店铺一定是可用的
        shopWhere.setEnableStatus(1);
        O2oExecution<Shop> shopO2oExecution = shopService.getShopList(shopWhere, page, 3);
        User user = (User) request.getSession().getAttribute("user");
        if(shopO2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
            modelMap.put("success", true);
            modelMap.put("pageInfo", null);
            modelMap.put("user", user);
        } else {
            modelMap.put("user", user);
            modelMap.put("success", true);
            modelMap.put("pageInfo", shopO2oExecution.getPageInfo());
        }

        return modelMap;
    }



    @RequestMapping("/showFrontEndList")
    @ResponseBody
    public Map<String, Object> showFrontEndList(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        //传入null值获得所有一级店铺分类列表(即prentId == null)
        O2oExecution<ShopCategory> shopCategoryO2oExecution = shopCategoryService.getShopCategoryByWhere(null);
        O2oExecution<HeadLine> headLineO2oExecution = headLineService.getHeadLineList();

        if(shopCategoryO2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
            return ModelMapUtil.errorMsg(shopCategoryO2oExecution.getStateInfo(), modelMap);
        }
        if(headLineO2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
            return ModelMapUtil.errorMsg(shopCategoryO2oExecution.getStateInfo(), modelMap);
        }
        User user = (User) request.getSession().getAttribute("user");
        modelMap.put("user", user);
        modelMap.put("success", true);
        modelMap.put("shopCategoryList", shopCategoryO2oExecution.getList());
        modelMap.put("headLineList", headLineO2oExecution.getList());
        return modelMap;
    }

    @RequestMapping("/showProductPageInfo")
    @ResponseBody
    public Map<String, Object> showProductPageInfo(Long shopId, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        if (shopId == null) {
            return ModelMapUtil.errorMsg("非法修改地址栏", modelMap);
        }
        O2oExecution<ProductCategory> o2oExecution = productCategoryService.getProductCategoryByShopId(shopId);
        O2oExecution<Shop> shopO2oExecution = shopService.getShopById(shopId);

        if (o2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
            return ModelMapUtil.errorMsg(o2oExecution.getStateInfo(), modelMap);
        }
        if (shopO2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
            return ModelMapUtil.errorMsg(shopO2oExecution.getStateInfo(), modelMap);
        }
        User user = (User) request.getSession().getAttribute("user");
        modelMap.put("user", user);
        modelMap.put("success", true);
        modelMap.put("productCategroyList", o2oExecution.getList());
        modelMap.put("shop", shopO2oExecution.getT());
        return modelMap;
    }

    /**
     * 前端系统显示该店铺下所有的商品列表
     * @param product
     * @param shopId
     * @param page
     * @param request
     * @return
     */
    @RequestMapping("/showProductList")
    @ResponseBody
    public Map<String, Object> showProductList(Product product, Long shopId, Integer page, HttpServletRequest request) {
        //这里不需要判断Product中的productName与productCategory.productCategoryId,因为这些再dao层已经判断过了
        Map<String, Object> modelMap = new HashMap<>();
        if (shopId == null) {
            return ModelMapUtil.errorMsg("非法修改地址栏！", modelMap);
        }
        product.setEnableStatus(1);
        O2oExecution<Product> o2oExecution = productService.getProductListByWhere(shopId, product, page);
        User user = (User) request.getSession().getAttribute("user");
        if (o2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
            modelMap.put("success", true);
            modelMap.put("user", user);
            modelMap.put("pageInfo", null);
        } else {

            modelMap.put("user", user);
            modelMap.put("success", true);
            modelMap.put("pageInfo", o2oExecution.getPageInfo());
        }
        return modelMap;
    }

    /**
     * 下面都是页面的转发
     */

    /**
     * 跳转到前台系统店铺集合页面
     * @return
     */
    @RequestMapping("/frontEndShopPage")
    public String frontEndShop() {
        return "frontend/frontendshop";
    }

    /**
     * 跳转到前台系统店铺详情页面
     * @return
     */
    @RequestMapping("/shopDetailPage")
    public String shopDetail() {
        return "frontend/shopdetail";
    }




}
