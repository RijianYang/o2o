package com.ahead.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/3/17
 */
@Data
public class EchartsXaxis {

    private String type = "category";
    private List<String> data = new ArrayList<>();
}
