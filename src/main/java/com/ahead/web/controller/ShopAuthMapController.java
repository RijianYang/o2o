package com.ahead.web.controller;

import com.ahead.dto.O2oExecution;
import com.ahead.dto.UserAccessToken;
import com.ahead.dto.WechatInfo;
import com.ahead.dto.WechatUser;
import com.ahead.enums.CommonStateEnum;
import com.ahead.exceptions.ControllerRuntimeException;
import com.ahead.pojo.ShopAuthMap;
import com.ahead.pojo.WechatAuth;
import com.ahead.service.ShopAuthMapService;
import com.ahead.service.WechatAuthService;
import com.ahead.util.CodeUtil;
import com.ahead.util.JsonUtil;
import com.ahead.util.ModelMapUtil;
import com.ahead.util.ShortNetAddressUtil;
import com.ahead.util.wechat.WechatUtil;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/11
 */
@Controller
@RequestMapping("/shopAuthMap")
public class ShopAuthMapController {

    /**
     * 用@Value主键取值的话，对应的配置文件一定要在springmvc的配置文件中引入，不然获取不到值
     * （因为子容器获取不到父容器中的配置文件的值）
     */
    @Value("${wechat.prefix}")
    private String urlPrefix;
    @Value(("${wechat.middle}"))
    private String urlMiddle;
    @Value("${wechat.appid}")
    private String urlAppid;
    @Value("${wechat.suffix}")
    private String urlSuffix;
    @Value("${wechat.auth.url}")
    private String authUrl;

    @Autowired
    private ShopAuthMapService shopAuthMapService;
    @Autowired
    private WechatAuthService wechatAuthService;

    /**
     * 根据微信回传过来的参数添加店铺的授权信息
     * @param request
     * @return
     */
    @RequestMapping("/addShopAuthMap")
    public String addShopAuthMap(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        //从request里面获取微信用户的信息
        String code = request.getParameter("code");
        try {
            UserAccessToken userAccessToken = WechatUtil.getUserAccessToken(code);
            WechatUser wechatUser = WechatUtil.getUserInfo(userAccessToken.getAccessToken(), userAccessToken.getOpenId());
            //防止店铺操作员没有在平台注册，这里注册一下，如果已注册，该方法内会自行处理
            O2oExecution<WechatAuth> o2oExecution = wechatAuthService.saveWechatAuth(wechatUser, "");
            //Service层有任何错误都会抛出异常然后被AOP异常捕获到所以这里直接获取到用户信息
            WechatAuth wechatAuth = o2oExecution.getT();
            //解析微信回传过来的自定义参数state，由于之前进行了编码，这里需要解码一下
            String qrCodeInfo = new String(
                    URLDecoder.decode(request.getParameter("state"), "UTF-8")
            );
            //转换为实体类
            WechatInfo wechatInfo = JsonUtil.jsonToPojo(qrCodeInfo, WechatInfo.class);
            //校验二维码是否过期
            if (!CodeUtil.isPastDue(wechatInfo.getCreateTime())) {
                return "wechat/qrcodepastdue";
            }
            //进行授权
            ShopAuthMap shopAuthMap = new ShopAuthMap();
            shopAuthMap.setTitle("员工");
            shopAuthMap.setTitleFlag(1);
            O2oExecution<ShopAuthMap> o = shopAuthMapService.addShopAuthMap(shopAuthMap, wechatAuth.getUser(), wechatInfo.getShopId());
            if (o.getState() == CommonStateEnum.EMPTY.getState()) {
                return "wechat/reshopauthmap";
            } else {
                return "wechat/success";
            }
        } catch (IOException e) {
            throw new ControllerRuntimeException(e.toString());
        }

    }


    /**
     * 该方法包括前台的编辑的功能、删除功能、复原功能（因为在奖品和商品操作那里会记录是谁操作的，如果真的删除，那边就引用不了了）
     * @param shopAuthMap
     * @param isCode
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping("/modifyShopAuthMapById")
    public Map<String, Object> modifyShopAuthMapById(ShopAuthMap shopAuthMap, boolean isCode, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        if (isCode == true) {
            if (!CodeUtil.checkVerifyCode(request)) {
                return ModelMapUtil.errorMsg("验证码输入错误", modelMap);
            }
        }

        O2oExecution<ShopAuthMap> o2oExecution = shopAuthMapService.modifyShopAuthMap(shopAuthMap);
        if (o2oExecution.getState() == CommonStateEnum.SUCCESS.getState()) {
            return ModelMapUtil.success(modelMap);
        }
        return ModelMapUtil.errorMsg("操作异常！", modelMap);
    }

    @RequestMapping("/getShopAuthMapById")
    @ResponseBody
    public Map<String, Object> getShopAuthMapById(Long shopAuthMapId) {
        Map<String, Object> modelMap = new HashMap<>();
        if (shopAuthMapId == null) {
            return ModelMapUtil.errorMsg("非法修改地址栏！", modelMap);
        }
        ShopAuthMap s = new ShopAuthMap();
        s.setShopAuthMapId(shopAuthMapId);
        O2oExecution<ShopAuthMap> o2oExecution = shopAuthMapService.getShopAuthMapById(s);
        if (o2oExecution.getState() == CommonStateEnum.SUCCESS.getState()) {
            modelMap.put("success", true);
            modelMap.put("shopAuthMap", o2oExecution.getT());
        } else {
            modelMap.put("success", true);
            modelMap.put("shopAuthMap", null);
        }
        return modelMap;
    }

    @RequestMapping(value = "/getShopAuthMapList")
    @ResponseBody
    public Map<String, Object> getShopAuthMapList(Long shopId, Integer page, Integer pageSize) {
        Map<String, Object> modelMap = new HashMap<>();
        O2oExecution<ShopAuthMap> o2oExecution = shopAuthMapService.getShopAuthMapListByShopId(shopId, page, 2);
        if (o2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
            return ModelMapUtil.errorMsg(o2oExecution.getStateInfo(), modelMap);
        } else {
            modelMap.put("success", true);
            modelMap.put("pageInfo", o2oExecution.getPageInfo());
            return modelMap;
        }
    }

    @RequestMapping("/generateQRCodeForShopAuth")
    public void generateQRCodeForShopAuth(Long shopId, HttpServletResponse response) {
        //获取当前时间戳，以保证二维码的时间有效性，精确到毫秒
        long time = System.currentTimeMillis();
        //将店铺id和time传入conten，赋值到state中，这样微信获取到这些信息后会回传到授权信息的添加的
        String content = "{\"shopId\":" + shopId + ",\"createTime\":" + time + "}";
        try {
            //将content的信息先进行编码以避免特殊字符造成的干扰，之后拼接目标URL
            String longUrl = urlPrefix + authUrl + urlMiddle + URLEncoder.encode(content, "UTF-8") + urlSuffix;
            //将目标URL转换成短的URL(太长的URL二维码会生成失败)
            String shortUrl = ShortNetAddressUtil.createShortUrl(longUrl);
            //调用二维码生成的工具类方法，传入的短的URL，生成二维码
            BitMatrix qrCodeImg = CodeUtil.generateQRCodeStream(shortUrl, response);
            //将二维码以图片流的形式输出到前端
            MatrixToImageWriter.writeToStream(qrCodeImg, "png", response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 以下都是转发的页面
     */

    @RequestMapping("/shopAuthMapListPage")
    public String shopAuthMapListPage() {
        return "shopauthmap/shopauthmaplist";
    }

    @RequestMapping("/shopAuthMapEditPage")
    public String shopAuthMapEditPage() {
        return "shopauthmap/shopauthmapedit";
    }
}
