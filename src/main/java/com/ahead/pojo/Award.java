package com.ahead.pojo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 奖品
 * @author Yang
 * @version 1.0
 * @time 2019/3/5
 */
@Data
public class Award {
    private Long awardId;

    @NotBlank(message = "请填写奖品名称")
    @Length(max = 10, min = 2, message = "奖品名称为2-10之间")
    private String awardName;

    @NotBlank(message = "请填写奖品描述")
    @Length(max = 300, message = "描述最多只能为300")
    private String awardDesc;

    /**
     * 奖品图片地址
     */
    private String awardImg;

    /**
     * 需要多少积分可以领取
     */
    @NotNull(message = "请填写积分")
    private Integer point;

    @NotNull(message = "请填写商品优先级")
    private Integer priority;
    private Date createTime;
    private Date lastEditTime;

    /**
     * 可用状态 0不可用，1可用
     */
    private Integer enableStatus;

    /**
     * 属于哪个店铺
     */
    private Shop shop;
}
