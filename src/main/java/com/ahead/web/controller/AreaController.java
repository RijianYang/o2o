package com.ahead.web.controller;

import com.ahead.dto.O2oExecution;
import com.ahead.enums.CommonStateEnum;
import com.ahead.pojo.Area;
import com.ahead.service.AreaService;
import com.ahead.util.ModelMapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Yang
 * @Date: 2019/1/17 19:39
 * @Version 1.0
 */
@Controller
@RequestMapping("/area")
public class AreaController {

    Logger logger = LoggerFactory.getLogger(AreaController.class);

    @Autowired
    private AreaService areaService;

    @RequestMapping("/listArea")
    @ResponseBody
    public Map<String, Object> listArea() {
        logger.info("===start===");
        long start = System.currentTimeMillis();
        Map<String, Object> modelMap = new HashMap<>();
        List<Area> areaList = new ArrayList<>();

        O2oExecution<Area> o2oExecution = areaService.getAreaList();
        if(o2oExecution.getState() != CommonStateEnum.SUCCESS.getState()) {
            return ModelMapUtil.errorMsg(o2oExecution.getStateInfo(), modelMap);
        }
        modelMap.put("rows", areaList);
        modelMap.put("total", areaList.size());

        logger.error("test error");
        long end = System.currentTimeMillis();
        logger.debug("costTime:[{}ms]", end - start);
        logger.info("===end===");
        return modelMap;
    }

}


