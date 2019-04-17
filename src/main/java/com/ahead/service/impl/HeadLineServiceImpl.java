package com.ahead.service.impl;

import com.ahead.dto.O2oExecution;
import com.ahead.mapper.HeadLineMapper;
import com.ahead.pojo.HeadLine;
import com.ahead.service.HeadLineService;
import com.ahead.util.JedisPoolClient;
import com.ahead.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/29
 */
@Service
public class HeadLineServiceImpl implements HeadLineService {

    private static final String HEADLINE_LINE_KEY = "head_line_list";
    @Autowired
    private HeadLineMapper headLineMapper;
    @Autowired
    private JedisPoolClient jedisPoolClient;

    @Override
    public O2oExecution<HeadLine> getHeadLineList() {

        O2oExecution<HeadLine> o2oExecution = null;
        List<HeadLine> headLineList = null;
        //从Redis中获取
        String headLineListJson = jedisPoolClient.get(HEADLINE_LINE_KEY);
        //如果为空就从数据库中查再存到Redis中
        if (StringUtils.isEmpty(headLineListJson)) {
            headLineList = headLineMapper.selectHeadLineList();
            jedisPoolClient.set(HEADLINE_LINE_KEY, JsonUtil.objectToJson(headLineList));
        } else {
            //如果从Redis中查出的数据非空就直接返回使用
            headLineList = JsonUtil.jsonToList(headLineListJson, HeadLine.class);
        }
        //判断返回的集合是否为空，然后进行相应的处理，这里会初始化o2oExecution
        o2oExecution = O2oExecution.isEmpty(o2oExecution, headLineList);

        return o2oExecution;
    }
}
