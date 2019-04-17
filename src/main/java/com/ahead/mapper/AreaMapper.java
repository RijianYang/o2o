package com.ahead.mapper;

import java.util.List;

import com.ahead.pojo.Area;

/**
 * @Author: Yang
 * @Date: 2019/1/16 16:20
 * @Version 1.0
 */
public interface AreaMapper {

	/**
	 * 获得所有的区域
	 * @return
	 */
	List<Area> getAreaList();
}
