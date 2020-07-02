package com.my.pojo.BO;

import lombok.Data;

/**
 * @Author xzw
 * @Date 2020/7/2
 */
@Data
public class ShopcartBO {
    private String itemId;
    private String itemImgUrl;
    private String itemName;
    private String specId;
    private String specName;
    private Integer buyCounts;
    private String priceDiscount;
    private String priceNormal;
}
