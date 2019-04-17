package com.ahead.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/2/25
 */
public class RequestUtil {

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static String requestURL(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        Map parameterMap = request.getParameterMap();
        //如果不为空就进行参数拼装
        if (parameterMap.size() > 0) {
            requestURI = requestURI + "?";
            StringBuffer  sb = new StringBuffer(requestURI);
            for (Object key : parameterMap.keySet()) {
                sb.append( key + "=" + request.getParameter((String)key) + "&");

            }
            requestURI = sb.toString().substring(0, sb.toString().length() - 1);
        }
        //否则就把原来的URL编码返回
        BASE64Encoder encoder = new BASE64Encoder();
        String encode = encoder.encode(requestURI.getBytes());
        return encode;

    }

    public static boolean ajaxRedirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
        //如果request.getHeader("X-Requested-With") 返回的是"XMLHttpRequest"说明就是ajax请求，需要特殊处理
        if("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))){
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(" {\"isRedirect\":true, \"loginURL\":\""+path+"\"}");
        } else {
            response.sendRedirect(path);
        }
        return false;
    }
}
