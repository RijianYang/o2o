package com.ahead.web.controller;

import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.pojo.Shop;
import com.ahead.pojo.User;
import com.ahead.pojo.UserAwardMap;
import com.ahead.service.UserAwardMapService;
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
 * @time 2019/3/20
 */
@Controller
@RequestMapping("/userAwardMap")
public class UserAwardMapController {

    @Autowired
    private UserAwardMapService userAwardMapService;

    @RequestMapping("/getUserAwardMapList")
    @ResponseBody
    public Map<String, Object> getUserAwardMapList(Long shopId, String buyerName, Integer page) {
        Map<String, Object> modelMap = new HashMap<>();
        Shop shop = new Shop();
        shop.setShopId(shopId);
        User buyer = new User();
        buyer.setName(buyerName);
        UserAwardMap userAwardMapWhere = new UserAwardMap();
        userAwardMapWhere.setUsedStatus(1);
        userAwardMapWhere.setShop(shop);
        userAwardMapWhere.setBuyer(buyer);
        O2oExecution<UserAwardMap> o2oExecution = userAwardMapService.getUserAwardMapList(userAwardMapWhere, page, 2);
        if (o2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
            return ModelMapUtil.errorMsg(o2oExecution.getStateInfo(), modelMap);
        } else {
            modelMap.put("pageInfo", o2oExecution.getPageInfo());
            modelMap.put("success", true);
            return modelMap;
        }
    }

    @RequestMapping("/userAwardMapListPage")
    public String userAwardMapListPage() {
        return "userawardmap/userawardmaplist";
    }

}
