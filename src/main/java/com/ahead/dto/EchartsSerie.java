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
public class EchartsSerie {

    private String type = "bar";
    private String name;
    private List<Integer> data = new ArrayList<>();
}
