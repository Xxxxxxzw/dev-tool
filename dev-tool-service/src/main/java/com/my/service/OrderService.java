package com.my.service;

import com.my.pojo.BO.SubmitOrderBO;
import com.my.pojo.ItemsSpec;

/**
 * @Author xzw
 * @Date 2020/7/16
 */
public interface OrderService {
    /**
     * 用于创建订单相关信息
     * @param submitOrderBO
     */
    public String createOrder(SubmitOrderBO submitOrderBO);


}
