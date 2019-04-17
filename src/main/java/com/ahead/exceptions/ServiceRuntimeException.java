package com.ahead.exceptions;

/**
 * @Author: Yang
 * @Date: 2019/1/17 10:37
 * @Version 1.0
 */
public class ServiceRuntimeException extends RuntimeException{

    public ServiceRuntimeException(String msg){
        super(msg);
    }
}
