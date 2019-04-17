package com.ahead.enums;

/**
 * @author Yang
 * @version 1.0
 * @time 2019/1/29
 */
public enum CommonStateEnum implements O2oStateEnum {
    /**
     * 操作成功
     */
    SUCCESS(1, "创建成功"),

    /**
     * 查找数据库的结果为空
     */
    EMPTY(-1001, "结果为空"),

    /**
     * 增删改受影响的行数为0（也就是增删改失败）
     */
    EFFECT_NUM_ZERO(-1002, "操作失败"),

    /**
     * 运行时出现了异常
     */
    INNER_ERROR(-1003, "系统运行异常"),

    /**
     * 传来的参数为空
     */
    PARAM_EMPTY(-1004, "参数为空");

    private int state;

    private String stateInfo;

    private CommonStateEnum(int state, String stateInfo) {
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
