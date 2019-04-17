package com.ahead.web.interceptor;

import com.ahead.pojo.User;
import com.ahead.util.RequestUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 店铺管理后台拦截器
 * @author Yang
 * @version 1.0
 * @time 2019/3/4
 */
public class ShopInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String reuqestURL = RequestUtil.requestURL(request);
        //1、从Session中获取User
        User user = (User) request.getSession().getAttribute("user");
        String userType = request.getParameter("userType");
        //2、如果登录了且用户类型不是店家类型就跳转到登录页面
        if (user != null && user.getUserType() != 2) {
            return RequestUtil.ajaxRedirect(request, response, "/o2o/localAuth/loginPage?userType=2&url="+reuqestURL);
        }
        //3、如果没有登录就跳转到登录页面
        if (user == null) {
            return RequestUtil.ajaxRedirect(request, response, "/o2o/localAuth/loginPage?userType=2&url="+reuqestURL);
        }
        //否则就放行
        return true;
    }
}