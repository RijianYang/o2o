package com.ahead.config;

import com.ahead.util.DesUtil;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Objects;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/2/27
 */
public class EncryptPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    /**
     * 需要加密的字段数组
     */
    private String[] encryptPropertyNames = {"jdbc.user", "jdbc.password", "redis.password"};

    /**
     * 获取配置文件中每个键值对后都会调用该方法
     * @param propertyName
     * @param propertyValue
     * @return
     */
    @Override
    protected String convertProperty(String propertyName, String propertyValue) {
        //判断是否已经进行了加密
        if(isEncrypt(propertyName)) {
            //对加密后的字符串进行解密操作
            String decryptValue = DesUtil.getDecryptString(propertyValue);
            return decryptValue;
        } else {
            return propertyValue;
        }
    }

    /**
     * 判断该属性name对应的值是否已加密
     * @param propertyName
     * @return
     */
    private boolean isEncrypt(String propertyName) {
        for (String encryptPropertyName : encryptPropertyNames) {
            if(Objects.equals(propertyName, encryptPropertyName)) {
                return true;
            }
        }
        return false;
    }
}
