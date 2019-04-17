package com.ahead.enums;

/**
 * @Author: Yang
 * @Date: 2019/1/17 7:10
 * @Version 1.0
 */
public enum ShopStateEnum implements O2oStateEnum {
    /**
     *  自定义的状态枚举实例
     */
    CHECK(0,"审核中"),OFFLINE(-1,"非法店铺"),SUCCESS(1,"操作成功"),INNER_ERROR(-1001,"内部系统错误"),
    NULL_SHOPID(-1002,"shopID为空"),NULL_SHOP(-1003,"Shop为空"),EMPTY(-1004,"结果为空"),EFFECT_NUM_ZERO(-1005, "操作失败");

    /**
     * 状态码
     */
    private int state;

    /**
     * 状态信息
     */
    private String stateInfo;

    private ShopStateEnum(int state,String stateInfo){
        this.state = state;
        this.stateInfo = stateInfo;
    }

    /**
     * 根据传入不同的state返回不同的Enum
     * @param state
     * @return
     */
    public static ShopStateEnum stateOf(int state){
        for(ShopStateEnum shopStateEnum : values()) {
            if(shopStateEnum.getState() == state){
                return shopStateEnum;
            }
        }
        return null;
    }

    @Override
    public int getState() {
        return state;
    }
    @Override
    public String getStateInfo() {
        return stateInfo;
    }
}
