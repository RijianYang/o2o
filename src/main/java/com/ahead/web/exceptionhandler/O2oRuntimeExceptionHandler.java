package com.ahead.web.exceptionhandler;

import com.ahead.exceptions.ControllerRuntimeException;
import com.ahead.exceptions.ServiceRuntimeException;
import com.ahead.util.ModelMapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/2/26
 */
@ControllerAdvice
public class O2oRuntimeExceptionHandler {

    Logger logger = LoggerFactory.getLogger(O2oRuntimeExceptionHandler.class);

    /**
     * ServiceRuntimeException异常，AOP会进行打印日志
     * @param e
     * @return
     */
    @ExceptionHandler(ServiceRuntimeException.class)
    @ResponseBody
    public Map<String, Object> handleServiceRuntimeException (ServiceRuntimeException e) {
        Map<String, Object> map = new HashMap<>();
        return ModelMapUtil.errorMsg(e.getMessage(), map);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Map<String, Object> handleRuntimeException (RuntimeException e) {
        Map<String, Object> map = new HashMap<>();
        return ModelMapUtil.errorMsg(e.getMessage(), map);
    }

    @ExceptionHandler(ControllerRuntimeException.class)
    @ResponseBody
    public Map<String, Object> handleControllerRuntimeException (ControllerRuntimeException e) {
        logger.error("Controller层异常！" + e.getMessage());
        Map<String, Object> map = new HashMap<>();
        return ModelMapUtil.errorMsg("请求异常！", map);
    }

}
