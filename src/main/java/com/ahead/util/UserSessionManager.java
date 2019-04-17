package com.ahead.util;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/4/16
 */
@Component
public class UserSessionManager {

    //userId : sessionId
    private ConcurrentHashMap<String, String> userSessionMap = new ConcurrentHashMap<>();
    //sessionId : HttpSession对象
    private ConcurrentHashMap<String, HttpSession> sessionMap = new ConcurrentHashMap<>();

    /**
     * UserSessionMap中增加一条键值对
     * @param userId
     * @param sessionId
     */
    public void putUserSessionMap(String userId, String sessionId) {
        userSessionMap.put(userId, sessionId);
    }

    /**
     * 根据SessionId删除对应的键值对
     * @param sessionId
     */
    public void removeInUserSessionMap(String sessionId) {
        String key = null;
        for (Map.Entry<String, String> entry : userSessionMap.entrySet()) {
            String value = entry.getValue();
            if (Objects.equals(value, sessionId)) {
                key = entry.getKey();
                break;
            }
        }
        if (key != null) {
            userSessionMap.remove(key);
        }
    }

    /**
     * 根据userId去UserSessionMap中获取对应的SessionId
     * @param userId
     */
    public String getSessionIdByUserId(String userId) {
        return userSessionMap.get(userId);
    }

    /**
     * 往SessionMap中增加一条键值对
     * @param sessionId
     * @param session
     */
    public void putSessionMap(String sessionId, HttpSession session) {
        sessionMap.put(sessionId, session);
    }

    /**
     * 根据sessionId销毁HttpSession对象
     * @param sessionId
     */
    public void destoryInSessionMap(String sessionId) {

        HttpSession session = sessionMap.get(sessionId);
        if (null != session) {
            session.invalidate();
        }
        //如果包含当前的key，且获取到的session对象又为null，说明对应的HttpSession对象过期了，
        //需要移除到对应的key，节省内存
        if (sessionMap.contains(sessionId) && session == null) {
            sessionMap.remove(sessionId);
        }
    }


    /**
     * 处理多用户登录，登录成功时调用该方法<br/>
     * 先根据userId去userSessionMap中获取对应的sessionId如果sessionId不为null且与当前
     * currentSessionId不一致的话就根据sessionId去sessionMap中获取对应的HttpSession对象并销毁
     * 否则的话就把当前的userId和currentSessionId存在UserSessionMap中
     *
     * @param currentSessionId
     * @param userId
     */
    public void handleMoreUserLogin(String userId, String currentSessionId) {
        String sessionId = getSessionIdByUserId(userId);
        if (null != sessionId && !Objects.equals(sessionId, currentSessionId)) {
            destoryInSessionMap(sessionId);
        }
        putUserSessionMap(userId, currentSessionId);
    }

}
