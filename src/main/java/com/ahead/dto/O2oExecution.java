package com.ahead.dto;

import com.ahead.enums.CommonStateEnum;
import com.ahead.enums.O2oStateEnum;
import com.ahead.enums.ShopStateEnum;
import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.util.List;

/**
 * @Author: Yang
 * @Date: 2019/1/17 7:03
 * @Version 1.0
 */
@Data
public class  O2oExecution<T> {

    /**
     * 状态码
     */
    private int state;

    /**
     * 状态信息
     */
    private String stateInfo;


    private T t;


    private List<T> list;

    private PageInfo<T> pageInfo;

    public  O2oExecution() {
    }

    /**
     * 店铺操作失败调用的构造方法
     * @param o2oEnum
     */
    public O2oExecution(O2oStateEnum o2oEnum){
        this.state = o2oEnum.getState();
        this.stateInfo = o2oEnum.getStateInfo();
    }

    /**
     * 店铺操作成功调用的构造方法
     * @param o2oEnum
     * @param t
     */
    public O2oExecution(O2oStateEnum o2oEnum, T t) {
        this.state = o2oEnum.getState();
        this.stateInfo = o2oEnum.getStateInfo();
        this.t = t;
    }

    /**
     * 店铺操作成功调用的构造方法
     * @param o2oEnum
     * @param list
     */
    public O2oExecution(O2oStateEnum o2oEnum, List<T> list) {
        this.state = o2oEnum.getState();
        this.stateInfo = o2oEnum.getStateInfo();
        this.list = list;
    }

    /**
     * 判断查到的集合是否为空 进行不同的处理
     * @param o2oExecution
     * @param list
     */
    public static O2oExecution isEmpty(O2oExecution o2oExecution, List list) {
        if(list == null) {
            o2oExecution = new O2oExecution<>(CommonStateEnum.EMPTY);
        } else {
            o2oExecution = new O2oExecution<>(CommonStateEnum.SUCCESS);
            o2oExecution.setList(list);
        }
        return o2oExecution;
    }


    /**
     * 判断查到的对象是否为空 进行不同的处理
     * @param o2oExecution
     * @param t
     */
    public static <T> O2oExecution<T> isEmpty(O2oExecution o2oExecution, T t) {
        if(t == null) {
            o2oExecution = new O2oExecution<>(CommonStateEnum.EMPTY);
        } else {
            o2oExecution = new O2oExecution<>(CommonStateEnum.SUCCESS);
            o2oExecution.setT(t);
        }
        return o2oExecution;
    }

}
