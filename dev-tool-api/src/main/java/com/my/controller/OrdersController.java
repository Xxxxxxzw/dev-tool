package com.my.controller;

import com.my.enums.PayMethod;
import com.my.pojo.BO.SubmitOrderBO;
import com.my.pojo.OrderItems;
import com.my.service.OrderService;
import com.my.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author xzw
 * @Date 2020/7/16
 */
@Api(value = "订单相关",tags = {"用于订单相关的API接口"})
@RestController
@RequestMapping("orders")
public class OrdersController {

    @Autowired
    OrderService orderService;

    @ApiOperation(value = "用户下单",notes = "用户下单",httpMethod = "POST")
    @PostMapping("create")
    public JSONResult create(@RequestBody SubmitOrderBO submitOrderBO,
                             HttpServletRequest request,
                             HttpServletResponse response){
        if (submitOrderBO.getPayMethod() != PayMethod.WEIXIN.type
                && submitOrderBO.getPayMethod() != PayMethod.ALIPAY.type ) {
            return JSONResult.errorMsg("支付方式不支持！");
        }
        String orderId = orderService.createOrder(submitOrderBO);
        return JSONResult.ok(orderId);
    }

}
