package com.ahead.service;

import java.util.List;

import com.ahead.dto.O2oExecution;
import com.ahead.pojo.Area;

public interface AreaService {
	/**
	 * 获得所有的区域
	 * @return
	 */
	O2oExecution<Area> getAreaList();

}
