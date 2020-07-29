package com.my.service;

import com.my.pojo.BO.SubmitOrderBO;
import com.my.pojo.ItemsSpec;
import com.my.pojo.OrderStatus;
import com.my.pojo.vo.OrderVO;

/**
 * @Author xzw
 * @Date 2020/7/16
 */
public interface OrderService {
    /**
     * 用于创建订单相关信息
     * @param submitOrderBO
     */
    public OrderVO createOrder(SubmitOrderBO submitOrderBO);

    /**
     * 修改订单状态
     * @param orderId
     * @param orderStatus
     */
    public void updateOrderStatus(String orderId, Integer orderStatus);

    /**
     * 查询订单状态
     * @param orderId
     * @return
     */
    public OrderStatus queryOrderStatusInfo(String orderId);
}
