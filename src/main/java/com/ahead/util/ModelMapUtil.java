package com.ahead.util;

import com.ahead.dto.O2oExecution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/24
 */
public class  ModelMapUtil <T> {

    /**
     * 返回捕获到异常的信息
     * @param e
     * @return
     */
    public static Map<String, Object> ExceptionMsg(Exception e, Map<String, Object> modelMap) {
        modelMap.put("success", false);
        modelMap.put("errorMsg", e.getMessage());
        return modelMap;
    }

    /**
     * 返回指定的异常信息
     * @param errorMsg 响应的异常信息
     * @param modelMap 封装信息的map
     * @return
     */
    public static Map<String, Object> errorMsg(String errorMsg, Map<String, Object> modelMap) {
        modelMap.put("success", false);
        modelMap.put("errorMsg", errorMsg);
        return modelMap;
    }

    /**
     * 按指定key把list存入modelMap中，返回成功信息
     * @param key
     * @param list
     * @param modelMap
     * @return
     */
    public static Map<String, Object> success(String key, List list, Map<String, Object> modelMap) {
        modelMap.put("success", true);
        modelMap.put(key, list);
        return modelMap;
    }

    /**
     * 仅仅返回一个信息
     * @param modelMap
     * @return
     */
    public static Map<String, Object> success( Map<String, Object> modelMap) {
        modelMap.put("success", true);
        return modelMap;
    }
}
