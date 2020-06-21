package com.my.pojo.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Arthur xzw
 * @Date 2020/6/20 23:23
 * @Description
 */
@Data
public class ItemCommentVO {
    private Integer commentLevel;
    private String content;
    private String specName;
    private Date createdTime;
    private String face;
    private String nickname;
}
