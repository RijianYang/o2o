package com.ahead.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/19
 */
public class ShortNetAddressUtil {

    private static Logger logger = LoggerFactory.getLogger(ShortNetAddressUtil.class);

    final static String CREATE_API = "https://dwz.cn/admin/v2/create";
    final static String TOKEN = "f8b38fed31a3197d754dfed053cc77a9";

    @Data
   static class UrlResponse {

        @JsonProperty("Code")
        private int code;

        @JsonProperty("ErrMsg")
        private String errMsg;

        @JsonProperty("LongUrl")
        private String longUrl;

        @JsonProperty("ShortUrl")
        private String shortUrl;

        /**
         * 百度短网址没有这个字段，不过每次通过Controller调用这里的静态方法，响应的json字符串中会带一个IsNew字段。
         */
        @JsonProperty("IsNew")
        private boolean isNew;
    }

    /**
     * 创建短网址
     *
     * @param longUrl
     *            长网址：即原网址
     * @return  成功：短网址
     *          失败：返回空字符串
     */
    public static String createShortUrl(String longUrl) {
        String params = "{\"url\":\""+ longUrl + "\"}";

        BufferedReader reader = null;
        try {
            // 创建连接
            URL url = new URL(CREATE_API);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            // 设置请求方式
            connection.setRequestMethod("POST");
            // 设置发送数据的格式
            connection.setRequestProperty("Content-Type", "application/json");
            //设置标识
            connection.setRequestProperty("Token", TOKEN);

            // 发起请求
            connection.connect();
            //将字节输出流转换为字符输出流
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            //点击append方法进去其实还是使用了writer方法，只不过多了空值判断
            out.append(params);
            //刷新并关闭
            out.flush();
            out.close();

            // 读取响应：将字节输入流转换为字符输入流并包装成缓冲字符输出流
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            String res = "";
            while ((line = reader.readLine()) != null) {
                res += line;
            }
            reader.close();

            // 抽取生成短网址
            UrlResponse urlResponse = JsonUtil.jsonToPojo(res, UrlResponse.class);
            if (urlResponse.getCode() == 0) {
                return urlResponse.getShortUrl();
            } else {
                return urlResponse.getErrMsg();
            }
        } catch (IOException e) {
            logger.error("createShortUrl error: " + e.toString());
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        String shortUrl = createShortUrl("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx7626c44e2aca26fd\\u0026redirect_uri=http://o2o.wangwangwangwangwang.wang/o2o/shop/addshopauthmap\\u0026response_type=code\\u0026scope=snsapi_userinfo\\u0026state=%7BaaashopIdaaa%3Anull%2CaaacreateTimeaaa%3A1552313071465%7D#wechat_redirect");
        System.out.println(shortUrl);
    }
}