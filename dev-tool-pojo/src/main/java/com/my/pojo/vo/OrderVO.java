package com.my.pojo.vo;

import lombok.Data;

/**
 * @Author xzw
 * @Date 2020/7/29
 */
@Data
public class OrderVO {
    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;
}
