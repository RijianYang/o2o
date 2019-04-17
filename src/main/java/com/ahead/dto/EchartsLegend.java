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
public class EchartsLegend {

    /**
     * 图例内容数组
     */
    private List<String> data = new ArrayList<>();
}
