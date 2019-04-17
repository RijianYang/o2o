package com.ahead.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.SecureRandom;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/2/27
 */
public class DesUtil {

    private static Key key;
    private static String KEY_STR = "myKey";
    private static String CHARSETNAME = "UTF-8";
    private static String ALGORITHM = "DES";

    static {
        try {
            //生成DES算法生成器
            KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
            //运用SHA1安全策略
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            //设置上密钥种子
            secureRandom.setSeed(KEY_STR.getBytes());
            //初始化基于SHA1安全策略的DES算法对象
            generator.init(secureRandom);
            //生成密钥对象
            key = generator.generateKey();
            generator = null;
        } catch (Exception e) {
            throw new RuntimeException("加密异常");
        }
    }

    /**
     * 获得加密后的字符串
     * @param str 需要加密的字符串
     * @return
     */
    public static String getEncryptString (String str) {
        //加密后的字节数组如果直接转换成String，会乱码，所以使用BASE64编码
        //基于BASE64编码，接受byte[] 转换成String
        BASE64Encoder base64Encoder = new BASE64Encoder();
        try {
            //将需要加密的字符串按UTF-8编码
            byte[] bytes = str.getBytes(CHARSETNAME);
            //获取加密对象
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            //初始化密码信息
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //传入需要加密的字节数组  返回加密后的字节数组
            byte[] doFinal = cipher.doFinal(bytes);
            //加密好的字节数组通过基于BASE64编码 编码成String并返回
            return base64Encoder.encode(doFinal);
        } catch (Exception e) {
            throw new RuntimeException("加密异常");
        }
    }

    /**
     * 获得解密后的字符串
     * @param str 需要解密的字符串
     * @return
     */
    public static String getDecryptString (String str) {
        //加密后的字节数组如果直接转换成String，会乱码，所以使用BASE64编码
        //基于BASE64编码，接受byte[] 转换成String
        BASE64Decoder base64Decoder = new BASE64Decoder();
        try {
            //将字符串通过基于BASE64编码 解码成字节数组（上面加密后的字节数组）
            byte[] bytes = base64Decoder.decodeBuffer(str);
            //获取解密对象
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            //初始化解密信息
            cipher.init(Cipher.DECRYPT_MODE, key);
            //传入需要解密的字节数组  返回解密后字节数组
            byte[] doFinal = cipher.doFinal(bytes);
            //返回解密之后的信息
            return new String(doFinal, CHARSETNAME);
        } catch (Exception e) {
            throw new RuntimeException("加密异常");
        }
    }

    public static void main(String[] args) {

    }

}
