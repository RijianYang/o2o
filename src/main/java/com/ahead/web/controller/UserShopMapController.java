package com.ahead.web.controller;

import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.pojo.Shop;
import com.ahead.pojo.User;
import com.ahead.pojo.UserShopMap;
import com.ahead.service.UserShopMapService;
import com.ahead.util.ModelMapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/19
 */
@Controller
@RequestMapping("/userShopMap")
public class UserShopMapController {

    @Autowired
    private UserShopMapService userShopMapService;

    @RequestMapping("/getUserShopMapList")
    @ResponseBody
    public Map<String, Object> getUserShopMapList(Long shopId, String buyerName, Integer page){
        Map<String, Object> modelMap = new HashMap<>();

        UserShopMap userShopMapWhere = new UserShopMap();
        Shop shop = new Shop();
        shop.setShopId(shopId);

        userShopMapWhere.setShop(shop);

        User buyer = new User();
        buyer.setName(buyerName);

        userShopMapWhere.setBuyer(buyer);
        O2oExecution<UserShopMap> o2oExecution = userShopMapService.getUserShopMapList(userShopMapWhere, page, 1);
        if (o2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
            return ModelMapUtil.errorMsg(o2oExecution.getStateInfo(), modelMap);
        } else {
            modelMap.put("success", true);
            modelMap.put("pageInfo", o2oExecution.getPageInfo());
            return modelMap;
        }
    }


    @RequestMapping("/userShopMapListPage")
    public String userShopMapListPage() {
        return "usershopmap/usershopmaplist";
    }
}
