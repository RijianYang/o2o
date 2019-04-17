package com.ahead.enums;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/24
 */
public enum ProductCategoryStateEnum implements O2oStateEnum{
    /**
     * 商品分类需要返回的几种声明
     */
    SUCCESS(1, "创建成功"),
    INNER_ERROR(-1001, "操作失败"),
    EMPTY_LIST(-1002, "添加数少于1"),
    PARAM_ERROR(-1003, "参数错误");

    private int state;

    private String stateInfo;

    private ProductCategoryStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
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
