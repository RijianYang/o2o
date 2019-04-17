package com.ahead.service;

import com.ahead.dto.O2oExecution;
import com.ahead.pojo.HeadLine;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/29
 */
public interface HeadLineService {

    /**
     * 获得所有上架的头条
     * @return
     */
    O2oExecution<HeadLine> getHeadLineList();
}
