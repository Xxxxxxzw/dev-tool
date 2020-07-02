package com.my.pojo.vo;

import lombok.Data;

/**
 * @Author xzw
 * @Date 2020/7/2
 */
@Data
public class ShopcartVO {
    private String itemId;
    private String itemImgUrl;
    private String itemName;
    private String specId;
    private String specName;
    private String priceDiscount;
    private String priceNormal;
}
