package com.ahead.dto;

import lombok.Data;

/**
 * 用来接收平台二维码的信息（因为之后有很多种类型的二维码）
 * @author Yang
 * @version 1.0
 * @time 2019/3/13
 */
@Data
public class WechatInfo {
    private Long customerId;
    private Long productId;
    private Long userAwardId;
    private Long createTime;
    private Long shopId;
}
