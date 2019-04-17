package com.ahead.web.interceptor;

import com.ahead.pojo.User;
import com.ahead.util.RequestUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 前端展示系统拦截器
 * @author Yang
 * @version 1.0
 * @time 2019/3/20
 */
public class FrontEndInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String reuqestURL = RequestUtil.requestURL(request);
            //1、从Session中获取User
            User user = (User) request.getSession().getAttribute("user");
            String userType = request.getParameter("userType");
            //2、如果没有登录就跳转到登录页面
            if (user == null) {
                return RequestUtil.ajaxRedirect(request, response, "/o2o/localAuth/loginPage?userType=1&url="+reuqestURL);
            }
            //否则就放行
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
