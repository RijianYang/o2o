package com.ahead.exceptions;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/13
 */
public class ControllerRuntimeException extends RuntimeException{

    public ControllerRuntimeException(String msg){
        super(msg);
    }
}
