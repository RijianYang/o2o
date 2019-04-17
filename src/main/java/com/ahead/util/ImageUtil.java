package com.ahead.util;

import com.ahead.dto.ImgWrap;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @Author: Yang
 * @Date: 2019/1/16 21:04
 * @Version 1.0
 */
public class ImageUtil {

    /**
     * classpath的根路径
     */
    private static String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    private static final Random r = new Random();

    /**
     *
     * 产生简略图(处理传过来的文件)
     *
     * @param imgWrap   包装了图片的路径和输入流对象
     * @param targetAddr 目标目录(文件夹)
     * @param width 生成图片的宽度
     * @param height 生成图片的高度
     * @param compress 压缩率
     * @return 返回的是相当于项目图片路径的一个图片相对路径
     */
    public static String generateThumbnail(ImgWrap imgWrap, String targetAddr, int width, int height, float compress) {
        //获得一个随机名称
        String realFileName = getRandomFileName();
        //根据传过来的文件名称获得它的扩展名
        String extension = getFileExtension(imgWrap.getName());
        //这个方法会根据这个相对路径拼装成绝对路径然后创建该文件夹
        makeDirPath(targetAddr);
        //拼装成一个相对地址 (图片相对路径)
        String relativeAddr = targetAddr + realFileName + extension;
        //拼装成一个图片的绝对路径(获得项目中图片存储目录 + 图片相对路径)
        File dest = new File(PathUtil.getImgBasePath() + relativeAddr);
        try {
            //前台上传过来的图片经过缩略图处理存储在我们自定义的项目图片地址
            Thumbnails.of(imgWrap.getInputStream())
                    .size(width, height)
                    .watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File( basePath + "/watermark.jpg")), 0.25f)
                    .outputQuality(compress)
                    .toFile(dest);
        } catch (IOException e) {
            throw new RuntimeException("缩略图失败" + e.toString());
        }
        return relativeAddr;
    }


    /**
     * 在项目图片绝对路径中创建文件夹
     *
     * @param targetAddr
     */
    private static void makeDirPath(String targetAddr) {
        //拼装成一个绝对路径然后创建对应的文件夹
        String realPath = PathUtil.getImgBasePath() + targetAddr;
        File dirPath = new File(realPath);
        if (!dirPath.exists()) {
            //如果目标路径不存在就创建所有的文件夹
            dirPath.mkdirs();
        }
    }

    /**
     * 获得文件的扩展名
     *
     * @param fileName
     * @return
     */
    private static String getFileExtension(String fileName) {
        //获得文件名称
        String originalFilename = fileName;
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

    /**
     * 获得一个随机文件名称
     * 年月日时分秒+一个随机的5位数
     *
     * @return
     */
    public static String getRandomFileName() {
        String currentTime = sdf.format(new Date());
        //获得10000-99999一个随机的5位数
        int random = r.nextInt(89999) + 10000;
        return currentTime + random;
    }

    /**
     * storePath为目录就删除下面所有的文件，如果是文件就删除该文件
     *
     * @param storePath
     */
    public static void deleteFileOrPath(String storePath) {
        File storePathFile = new File(PathUtil.getImgBasePath() + storePath);
        if (storePathFile.exists()) {
            if (storePathFile.isDirectory()) {
                File[] files = storePathFile.listFiles();
                for (File file : files) {
                    file.delete();
                }
            } else {
                storePathFile.delete();
            }
        }
    }

}
