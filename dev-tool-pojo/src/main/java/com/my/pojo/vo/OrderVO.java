package com.my.pojo.vo;

import com.my.pojo.BO.ShopcartBO;
import lombok.Data;

import java.util.List;

/**
 * @Author xzw
 * @Date 2020/7/29
 */
@Data
public class OrderVO {
    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;
    private List<ShopcartBO> toBeRemovedShopcatdList;
}
