package com.ahead.service.impl;

import com.ahead.dto.O2oExecution;
import com.ahead.mapper.AreaMapper;
import com.ahead.pojo.Area;
import com.ahead.service.AreaService;
import com.ahead.util.JedisPoolClient;
import com.ahead.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Author: Yang
 * @Date: 2019/1/17 19:39
 * @Version 1.0
 */
@Service
public class AreaServiceImpl implements AreaService {

	@Autowired
	private AreaMapper areaMapper;
	@Autowired
	private JedisPoolClient jedisPoolClient;

	private static final String AREA_LIST_KEY = "area_list";

	@Override
	public O2oExecution<Area> getAreaList() {
		O2oExecution<Area> o2oExecution = null;
			List<Area> areaList = null;
			//从Redis中查，如果为空就去数据库中查
			String areaListJson = jedisPoolClient.get(AREA_LIST_KEY);
			if (StringUtils.isEmpty(areaListJson)) {
				areaList = areaMapper.getAreaList();
				//数据库中查到后存储到Redis中
				jedisPoolClient.set(AREA_LIST_KEY, JsonUtil.objectToJson(areaList));
			} else {
				//不为空就直接返回Redis中的数据
				areaList = JsonUtil.jsonToList(areaListJson, Area.class);
			}
			o2oExecution = O2oExecution.isEmpty(o2oExecution, areaList);
			return o2oExecution;
		}

	}

