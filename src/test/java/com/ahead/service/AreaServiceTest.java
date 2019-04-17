package com.ahead.service;

import com.ahead.BaseTest;
import com.ahead.dto.O2oExecution;
import com.ahead.pojo.Area;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/2/28
 */
public class AreaServiceTest extends BaseTest {

    @Autowired
    private AreaService areaService;

    @Test
    public void testGetAreaList() throws Exception {

        O2oExecution<Area> o2oExecution = areaService.getAreaList();
        System.out.println(o2oExecution.getList());
    }
}
