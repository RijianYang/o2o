package com.ahead.config;

import com.ahead.exceptions.ServiceRuntimeException;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/1
 */
public class AopExceptionAndPrintLog {
    private Logger logger;

    /**
     * 使用AOP处理异常和打印日志，切点抛出异常前调用的方法
     * @param joinpoint
     * @param ex
     */
    public void handleExceptionAndPrintLog(JoinPoint joinpoint, Exception ex) {
        Class<?> clazz = joinpoint.getTarget().getClass();
        logger = LoggerFactory.getLogger(clazz);
        if (ex instanceof  ServiceRuntimeException) {
            //如果属于自定义的异常那就是程序运行时手动抛出来的，就把自定义的错误信息打印到日志上
            //Service中的报错就会被异常处理器获取到进行处理
            logger.warn(ex.getMessage());
        } else {
            //如果不是自定义异常那么就是程序运行时报错了，这时候就把报错信息打印到日志上
            logger.error(getExceptionAllinformation(ex));
            //再抛出操作异常供前台 友好显示，
            // 这里抛出ServiceRuntimeException就直接被异常处理器获取到了异常处理器直接返回到视图解析器再到浏览器，所以后面真正的异常没有显示
            throw new ServiceRuntimeException("操作异常");
        }
    }

    /**
     * 获取异常信息
     * @param ex
     * @return
     */
    public String getExceptionAllinformation(Exception ex) {
        //创建一个字节数组输出流，会输出到内存中的字节数组中
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //包装成打印输出流
        PrintStream pout = new PrintStream(out);
        //把异常信息输出到该打印输出流中
        ex.printStackTrace(pout);
        //把前面输出到内存数组中的数据转换成字节数组然后返回异常字符串
        String ret = new String(out.toByteArray());
        pout.close();
        try {
            out.close();
        } catch (Exception e) {
        }
        return ret;
    }
}
