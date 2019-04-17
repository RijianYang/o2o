package com.ahead.web.controller;

import com.ahead.dto.O2oExecution;
import com.ahead.dto.UserAccessToken;
import com.ahead.dto.WechatInfo;
import com.ahead.dto.WechatUser;
import com.ahead.enums.CommonStateEnum;
import com.ahead.exceptions.ControllerRuntimeException;
import com.ahead.pojo.*;
import com.ahead.service.*;
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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 需要登录的前台系统页面
 * @author Yang
 * @version 1.0
 * @time 2019/3/20
 */
@Controller
@RequestMapping("/loginFrontEnd")
public class LoginFrontEndController {

    @Autowired
    private AwardService awardService;
    @Autowired
    private UserShopMapService userShopMapService;
    @Autowired
    private UserAwardMapService userAwardMapService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductImgService productImgService;
    @Autowired
    private WechatAuthService wechatAuthService;
    @Autowired
    private ShopAuthMapService shopAuthMapService;
    @Autowired
    private UserProductMapService userProductMapService;

    /**
     * 用@Value主键取值的话，对应的配置文件一定要在springmvc的配置文件中引入，不然获取不到值
     * （因为子容器获取不到父容器中的配置文件的值，能获取到父容器中的Bean）
     */
    @Value("${wechat.prefix}")
    private String urlPrefix;
    @Value(("${wechat.middle}"))
    private String urlMiddle;
    @Value("${wechat.appid}")
    private String urlAppid;
    @Value("${wechat.suffix}")
    private String urlSuffix;
    @Value("${wechat.sale-product.url}")
    private String saleProductUrl;
    @Value("${wechat.get-award.url}")
    private String getAwardUrl;


    @RequestMapping("/getUserProductMapList")
    @ResponseBody
    public Map<String, Object> getUserProductMapList(HttpServletRequest request, String productName, Integer page){
        Map<String, Object> modelMap = new HashMap<>();
        User user = (User) request.getSession().getAttribute("user");
        Product product = new Product();
        product.setProductName(productName);
        UserProductMap userProductMapWhere = new UserProductMap();
        userProductMapWhere.setBuyer(user);
        userProductMapWhere.setProduct(product);
        O2oExecution<UserProductMap> o2oExecution = userProductMapService.getUserProductListByWhere(userProductMapWhere, page, 4);
        if (o2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
            //为null说明该用户下没有对用户的消费记录
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

    @RequestMapping("/getUserShopMapList")
    @ResponseBody
    public Map<String, Object> getUserShopMapList(HttpServletRequest request, String shopName, Integer page) {
        Map<String, Object> modelMap = new HashMap<>();
        User user = (User) request.getSession().getAttribute("user");

        UserShopMap userShopMapWhere = new UserShopMap();
        Shop shop = new Shop();
        shop.setShopName(shopName);
        userShopMapWhere.setShop(shop);
        userShopMapWhere.setBuyer(user);
        O2oExecution<UserShopMap> o2oExecution = userShopMapService.getUserShopMapList(userShopMapWhere, page, 4);
        if (o2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
            modelMap.put("success", true);
            modelMap.put("pageInfo", null);
            modelMap.put("user", user);
        } else {
            modelMap.put("success", true);
            modelMap.put("pageInfo", o2oExecution.getPageInfo());
            modelMap.put("user", user);
        }
        return modelMap;
    }


    @RequestMapping("/getAward")
    public String getAward(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        //从request里面获取微信用户的信息
        String code = request.getParameter("code");
        try {
            UserAccessToken userAccessToken = WechatUtil.getUserAccessToken(code);
            WechatUser wechatUser = WechatUtil.getUserInfo(userAccessToken.getAccessToken(), userAccessToken.getOpenId());

            //先根据openId查询扫码的人是否在我们平台注册过
            O2oExecution<WechatAuth> wechatAuthO2oExecution = wechatAuthService.getWechatByOpenId(wechatUser.getOpenId());
            if (wechatAuthO2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
                //没有注册过就返回不是操作员页面
                return "wechat/notwechatauth";
            }

            //否则就获取该平台上的微信账号标识
            //Service层有任何错误都会抛出异常然后被AOP异常捕获到所以这里直接获取到用户信息
            WechatAuth wechatAuth = wechatAuthO2oExecution.getT();
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

            //查看扫码的人是否为该店铺的操作员
            ShopAuthMap shopAuthMapWhere = new ShopAuthMap();
            Shop shopWhere = new Shop();
            shopWhere.setShopId(wechatInfo.getShopId());
            shopAuthMapWhere.setShop(shopWhere);
            shopAuthMapWhere.setEmployee(wechatAuth.getUser());

            O2oExecution<ShopAuthMap> shopAuthMapO2oExecution = shopAuthMapService.getShopAuthMapById(shopAuthMapWhere);
            if (shopAuthMapO2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
                return "wechat/notoperator";
            }
            //如果是该店铺的操作员，就修改其用户奖品映射状态，并修改其操作员
            O2oExecution<UserAwardMap> o2oExecution = userAwardMapService.updateUserAwardMapById(wechatInfo.getUserAwardId(),
                    wechatAuth.getUser());
            if (o2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
                return "wechat/reget";
            } else {
                return "wechat/success";
            }
        } catch (Exception e) {
            throw new ControllerRuntimeException(e.toString());
        }
    }

    @RequestMapping("/generateQRCodeForGetAward")
    public void generateQRCodeForGetAward(Long userAwardMapId, Long shopId, HttpServletResponse response) {
        //获取当前时间戳，以保证二维码的时间有效性，精确到毫秒
        long time = System.currentTimeMillis();
        //将店铺id和time传入conten，赋值到state中，这样微信获取到这些信息后会回传到授权信息的添加的
        String content = "{\"userAwardId\": "+userAwardMapId+", \"createTime\": "+time+", \"shopId\": "+shopId+"}";
        try {
            //将content的信息先进行编码以避免特殊字符造成的干扰，之后拼接目标URL
            String longUrl = urlPrefix + getAwardUrl + urlMiddle + URLEncoder.encode(content, "UTF-8") + urlSuffix;
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

    @RequestMapping("/getUserAwardMapById")
    @ResponseBody
    public Map<String, Object> getUserAwardMapById(Long userAwardMapId, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        O2oExecution<UserAwardMap> o2oExecution = userAwardMapService.getUserAwardMapById(userAwardMapId);
        User user = (User) request.getSession().getAttribute("user");
        if (o2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
            return ModelMapUtil.errorMsg(o2oExecution.getStateInfo(), modelMap);
        } else {
            modelMap.put("success", true);
            modelMap.put("userAwardMap", o2oExecution.getT());
            modelMap.put("user", user);
            return modelMap;
        }
    }

    @RequestMapping("/saleProduct")
    public String saleProduct(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        //从request里面获取微信用户的信息
        String code = request.getParameter("code");
        try {
            UserAccessToken userAccessToken = WechatUtil.getUserAccessToken(code);
            WechatUser wechatUser = WechatUtil.getUserInfo(userAccessToken.getAccessToken(), userAccessToken.getOpenId());
            //先根据opid查询扫码的人是否在我们平台注册过
            O2oExecution<WechatAuth> wechatAuthO2oExecution = wechatAuthService.getWechatByOpenId(wechatUser.getOpenId());
            if (wechatAuthO2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
                //没有注册过就返回不是操作员页面
                return "wechat/notoperator";
            }
            //否则就获取该平台上的微信账号标识
            //Service层有任何错误都会抛出异常然后被AOP异常捕获到所以这里直接获取到用户信息
            WechatAuth wechatAuth = wechatAuthO2oExecution.getT();
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
            //查看扫码的人是否为该店铺的操作员
            ShopAuthMap shopAuthMapWhere = new ShopAuthMap();
            Shop shopWhere = new Shop();
            shopWhere.setShopId(wechatInfo.getShopId());
            shopAuthMapWhere.setShop(shopWhere);
            shopAuthMapWhere.setEmployee(wechatAuth.getUser());
            O2oExecution<ShopAuthMap> shopAuthMapO2oExecution = shopAuthMapService.getShopAuthMapById(shopAuthMapWhere);
            if (shopAuthMapO2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
                return "wechat/notoperator";
            }
            //否则添加的对应的销售记录，并在Service中添加对应的总积分(如果该顾客还没有注册该店铺的会员，就初始化)
            O2oExecution<UserProductMap> o = userProductMapService.addUserProductMap(wechatInfo.getCustomerId(), wechatInfo.getProductId(),
                    wechatInfo.getShopId(), wechatAuth.getUser().getUserId());
            if (o.getState() == CommonStateEnum.EMPTY.getState()) {
                return "wechat/error";
            } else {
                return "wechat/success";
            }
        } catch (Exception e) {
            throw new ControllerRuntimeException(e.toString());
        }
    }


    @RequestMapping("/generateQRCodeForSaleProduct")
    public void generateQRCodeForSaleProduct(Long productId, Long shopId, HttpServletResponse response, HttpServletRequest request) {
        //获取当前时间戳，以保证二维码的时间有效性，精确到毫秒
        long time = System.currentTimeMillis();
        User user = (User) request.getSession().getAttribute("user");
        //将店铺id和time传入conten，赋值到state中，这样微信获取到这些信息后会回传到授权信息的添加的
        String content = "{\"shopId\": "+shopId+", \"productId\": "+productId+", \"createTime\": "+time+", \"customerId\": "+user.getUserId()+"}";
        try {
            //将content的信息先进行编码以避免特殊字符造成的干扰，之后拼接目标URL
            String longUrl = urlPrefix + saleProductUrl + urlMiddle + URLEncoder.encode(content, "UTF-8") + urlSuffix;
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


    @RequestMapping("/getProductInfoById")
    @ResponseBody
    public Map<String, Object> getProductInfoById(Long productId, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        O2oExecution<Product> productO2oExecution = productService.getProductById(productId);
        O2oExecution<ProductImg> productImgO2oExecution = productImgService.getProductImgListById(productId);
        if (productO2oExecution.getState() == CommonStateEnum.EMPTY.getState() || productImgO2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
            //结果为EMPTY的话报错信息都是一样，所以这里只要响应一个错误信息就行了
            return ModelMapUtil.errorMsg(productO2oExecution.getStateInfo(), modelMap);
        } else {
            User user = (User) request.getSession().getAttribute("user");
            modelMap.put("success", true);
            modelMap.put("productImgList", productImgO2oExecution.getList());
            modelMap.put("product", productO2oExecution.getT());
            modelMap.put("user", user);
            return modelMap;
        }
    }

    @RequestMapping("/exchangeRecordList")
    @ResponseBody
    public Map<String, Object> exchangeRecordList(String awardName, HttpServletRequest request, Integer page) {
        Map<String, Object> modelMap = new HashMap<>();
        User user = (User) request.getSession().getAttribute("user");
        UserAwardMap userAwardMapWhere = new UserAwardMap();
        userAwardMapWhere.setBuyer(user);
        Award award = new Award();
        award.setAwardName(awardName);
        userAwardMapWhere.setAward(award);
        O2oExecution<UserAwardMap> o2oExecution = userAwardMapService.getUserAwardMapList(userAwardMapWhere, page, 4);
        if (o2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
            modelMap.put("success", true);
            modelMap.put("pageInfo", null);
            modelMap.put("user", user);
        } else {
            modelMap.put("success", true);
            modelMap.put("pageInfo", o2oExecution.getPageInfo());
            modelMap.put("user", user);
        }
        return modelMap;
    }

    @RequestMapping("/exchangeAward")
    @ResponseBody
    public Map<String, Object> exchangeAward(Long shopId, Long awardId, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        User user = (User) request.getSession().getAttribute("user");
        //Service会保存用户兑换奖品记录并减少对应的总积分
        O2oExecution<UserAwardMap> o2oExecution = userAwardMapService.saveUserAwardMap(user.getUserId(), awardId, shopId);
        //后面出错就会直接抛异常，这里直接返回就好
        return ModelMapUtil.success(modelMap);
    }

    @RequestMapping("/getAwardList")
    @ResponseBody
    public Map<String, Object> getAwardList(Long shopId, String awardName, HttpServletRequest request, Integer page) {
        Map<String, Object> modelMap = new HashMap<>();

        Award awardWhere = new Award();
        Shop shop = new Shop();
        shop.setShopId(shopId);

        awardWhere.setShop(shop);
        awardWhere.setAwardName(awardName);
        awardWhere.setEnableStatus(1);
        User user = (User) request.getSession().getAttribute("user");

        O2oExecution<Award> awardO2oExecution = awardService.getAwardList(awardWhere, page, 4);
        O2oExecution<UserShopMap> usmO2oExecution = userShopMapService.getUserShopMapById(shopId, user.getUserId());
        if (awardO2oExecution.getState() == CommonStateEnum.EMPTY.getState()) {
            //如果为空的话，错误信息都是一样的， 所以返回一个给前台显示就行了
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
     * 下面都是页面的转发
     * @return
     */

    /**
     * 转发到前端系统的某个店铺的奖品列表页面
     * @return
     */
    @RequestMapping("/awardListPage")
    public String awardListPage() {
        return "loginfrontend/awardlist";
    }

    /**
     * 转发到前端系统的顾客的兑换记录列表页面
     * @return
     */
    @RequestMapping("/exchangeRecordListPage")
    public String exchangeRecordListPage() {
        return "loginfrontend/exchangerecordlist";
    }

    /**
     * 转发到商品详情页面
     * @return
     */
    @RequestMapping("/productDetailPage")
    public String productDetailPage() {
        return "loginfrontend/productdetail";
    }

    /**
     * 转发到领取用户奖品映射页面
     * @return
     */
    @RequestMapping("/userAwardMapDetailPage")
    public String userAwardMapDetailPage() {
        return "loginfrontend/userawardmapdetail";
    }

    @RequestMapping("/userShopMapListPage")
    public String userShopMapListPage() {
        return "loginfrontend/usershopmaplist";
    }

    @RequestMapping("/userProductMapListPage")
    public String userProductMapListPage() {
        return "loginfrontend/userproductmaplist";
    }
}
