package com.ahead.web.listener;

import com.ahead.util.SpringUtils;
import com.ahead.util.UserSessionManager;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/4/16
 */
@WebListener
public class HttpSessionLoginListener implements HttpSessionListener {


    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        //监听到HttpSession被创建时，就把当前HttpSession对象存储在SessionMap中
        HttpSession session = httpSessionEvent.getSession();
        UserSessionManager userSessionManager = SpringUtils.getBean(UserSessionManager.class);
        userSessionManager.putSessionMap(session.getId(), session);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        //当有HttpSession对象被销毁时就删除UserSessionMap中的键值对
        String sessionId = httpSessionEvent.getSession().getId();
        UserSessionManager userSessionManager = SpringUtils.getBean(UserSessionManager.class);
        userSessionManager.removeInUserSessionMap(sessionId);
    }
}
