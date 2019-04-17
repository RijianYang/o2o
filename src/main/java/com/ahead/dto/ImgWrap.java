package com.ahead.dto;

import lombok.Data;

import java.io.InputStream;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/27
 */
@Data
public class ImgWrap {

    /**
     * 图片输入流
     */
    private InputStream inputStream;

    /**
     * 图片名称
     */
    private String name;

    public ImgWrap(){};

    public ImgWrap(InputStream inputStream, String name) {
        this.inputStream = inputStream;
        this.name = name;
    }
}
