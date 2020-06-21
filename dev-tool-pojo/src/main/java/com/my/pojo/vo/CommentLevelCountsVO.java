package com.my.pojo.vo;

import lombok.Data;

/**
 * @Arthur xzw
 * @Date 2020/6/20 17:42
 * @Description
 */
@Data
public class CommentLevelCountsVO {
    public Integer totalCounts;
    public Integer goodCounts;
    public Integer normalCounts;
    public Integer badCounts;
}
