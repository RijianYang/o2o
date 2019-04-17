package com.ahead.util;

/**
 * @Author: Yang
 * @Date: 2019/1/16 21:37
 * @Version 1.0
 */
public class PathUtil {

    /**
     * 获得系统路径的分割符
     */
    private static String seperator = System.getProperty("file.separator");

    /**
     * 获得项目图片的根路径
     * @return
     */
    public static String getImgBasePath() {
        //获得系统的名称
        String osName = System.getProperty("os.name");
        String basePath = "";
        if(osName.toLowerCase().startsWith("win")){
            basePath = "D:/项目所需路径/o2o_project/image";
        } else {
            //如果不是Windows系统
            basePath = "/home/Yang/image";
        }
        //防止分割符出错，下面动态的替换系统的分割符
        basePath.replace("/",seperator);
        return basePath;
    }

    /**
     * 根据业务需求返回项目中店铺的子目录(商品id命名的文件夹)
     * @param shopId
     * @return
     */
    public static String getShopImgPath(Long shopId){
        String imgPath = "/upload/images/item/shop/" + shopId + "/";
        return imgPath.replace("/",seperator);
    }

}
