package com.ahead.util;

import com.google.code.kaptcha.Constants;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/19
 */
public class CodeUtil {

    /**
     * 校验填写的验证码是否和生成验证码(会存在session中)的字符串一致
     * @param request
     * @return
     */
    public static boolean checkVerifyCode(HttpServletRequest request){
        String realVerifyCode = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        String requestVerifyCode = request.getParameter("verifyCode");
        if(requestVerifyCode == null || requestVerifyCode.trim().equals("")
                || !realVerifyCode.equals(requestVerifyCode)){
            return false;
        }
        return true;
    }

    /**
     * 生成二维码的图片流
     * @param content
     * @param response
     * @return
     */
    public static BitMatrix generateQRCodeStream(String content, HttpServletResponse response) {
        //给响应头添加头部信息，主要是告诉浏览器返回的是图片流
        //设置响应头告诉浏览器不要缓存
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        //这行代码设置了浏览器缓存页面的时限
        //这里设置成 0 ，表示不进行缓存。
        response.setDateHeader("Expires", 0);
        //设置响应的类型为图片png
        response.setContentType("image/png");
        //设置图片的文字编码以及内边框距
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix;
        try {
            //参数顺序分别为：编码内容（一般为url），编码类型，生成图片宽度，生成图片高度，设置参数
            bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 200, 200, hints);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
        return bitMatrix;
    }

    /**
     * 设置3分钟二维码过期
     * @param createTime
     * @return
     */
    public static boolean isPastDue(Long createTime) {
        long now = System.currentTimeMillis();
        if (createTime + (3 * 60 * 1000) < now) {
            return false;
        }
        return true;
    }
}

