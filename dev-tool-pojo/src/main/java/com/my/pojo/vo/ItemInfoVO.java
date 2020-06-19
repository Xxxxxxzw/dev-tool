package com.my.pojo.vo;

import com.my.pojo.Items;
import com.my.pojo.ItemsImg;
import com.my.pojo.ItemsParam;
import com.my.pojo.ItemsSpec;
import lombok.Data;

import java.util.List;

/**
 * @Author xzw
 * @Date 2020/6/19
 */
@Data
public class ItemInfoVO {
    Items item;
    List<ItemsImg> itemImgList;
    List<ItemsSpec> itemSpecList;
    ItemsParam itemParams;
}
