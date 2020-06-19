package com.my.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author xzw
 * @Date 2020/6/19
 */
@Data
public class NewItemsVO {
    private Integer rootCatId;
    private String rootCatName;
    private String slogan;
    private String catImage;
    private String bgColor;
    private List<SimpleItemsVO> simpleItemList;
}
