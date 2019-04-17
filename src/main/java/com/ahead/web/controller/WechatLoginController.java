package com.ahead.web.controller;

import com.ahead.dto.O2oExecution;
import com.ahead.dto.UserAccessToken;
import com.ahead.dto.WechatUser;
import com.ahead.enums.CommonStateEnum;
import com.ahead.pojo.User;
import com.ahead.pojo.WechatAuth;
import com.ahead.service.WechatAuthService;
import com.ahead.util.wechat.WechatUtil;
import com.ahead.config.AopExceptionAndPrintLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/2/24
 * 获取关注公众号之后的微信用户信息的接口，如果在微信浏览器里访问 （appid与访问的uri是根据自己怎么定义，其他都是微信规定的）
 * https://open.weixin.qq.com/connect/oauth2/authorize?appid=your appID&redirect_uri=your website domain name/o2o/wechatLogin/loginCheck&role_type=1&response_type=code&scope=snsapi_userinfo&state=1#wechat_redirect
 * 则这里将会获取到code,之后再可以通过code获取到access_token 进而获取到用户信息
 */
@Controller
@RequestMapping("/wechatLogin")
public class WechatLoginController {

    private static Logger log = LoggerFactory.getLogger(WechatLoginController.class);

    @Autowired
    private WechatAuthService wechatAuthService;
    @Autowired
    private AopExceptionAndPrintLog aopExceptionAndPrintLog;

    @RequestMapping(value = "/loginCheck", method = {RequestMethod.GET})
    public String doGet(HttpServletRequest request, HttpServletResponse response) {

        log.debug("weixin login get...");
        // 获取微信公众号传输过来的code,通过code可获取access_token,进而获取用户信息
        String code = request.getParameter("code");
        // 这个state可以用来传我们自定义的信息，方便程序调用，公众号上两个选择如果点击了顾客就是1，如果点击了店铺管理就是2
         String roleType = request.getParameter("state");
        log.debug("weixin login code:" + code);
        WechatUser wechatUser = null;
        UserAccessToken token = null;
        String openId = null;

        if (null != code) {
            try {
                //根据code请求微信得到token
                token = WechatUtil.getUserAccessToken(code);

                log.debug("weixin login token:" + token.toString());
                // 通过token获取accessToken
                String accessToken = token.getAccessToken();
                // 通过token获取openId
                openId = token.getOpenId();
                // 通过access_token和openId获取用户昵称等信息
                wechatUser = WechatUtil.getUserInfo(accessToken, openId);
                log.debug("weixin login user:" + wechatUser.toString());
                request.getSession().setAttribute("openId", openId);
            } catch (IOException e) {
                log.error(aopExceptionAndPrintLog.getExceptionAllinformation(e));
                return null;
            }
        }
        // ======todo begin======
        // 前面咱们获取到openId后，可以通过它去数据库判断该微信帐号是否在我们网站里有对应的帐号了，
        // 没有的话这里可以自动创建上，直接实现微信与咱们网站的无缝对接。
        // ======todo end======
        if(wechatUser != null && wechatUser.getOpenId() != null) {
            O2oExecution<WechatAuth> o2oExecution = wechatAuthService.saveWechatAuth(wechatUser, roleType);
            if(o2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
                return null;
            } else {
                //如果成功就把用户存在session中
                User user = o2oExecution.getT().getUser();
                request.getSession().setAttribute("user", user);
                //如果该用户账号不可用且访问的是店家后台系统就跳转到提示页面
                //如果不可用且访问的是前台系统就直接跳到前台页面
                if (user.getEnableStatus() == 0) {
                    return roleType.equals("1") ? "frontend/index" : "shop/active";
                } else {
                    //如果该用户账号可用且访问的是店家后台系统就跳转到店家后台管理系统
                    //如果可用且访问的是前台系统就直接跳到前台页面
                    return roleType.equals("1") ? "frontend/index" : "shop/shoplist";
                }

            }
        }
        return null;
    }
}