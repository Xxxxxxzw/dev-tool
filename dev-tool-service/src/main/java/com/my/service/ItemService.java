package com.my.service;

import com.my.pojo.Items;
import com.my.pojo.ItemsImg;
import com.my.pojo.ItemsParam;
import com.my.pojo.ItemsSpec;

import java.util.List;

/**
 * @Author xzw
 * @Date 2020/6/19
 */
public interface ItemService {
    public Items queryItemById(String itemId);

    public List<ItemsImg> queryItemsImgList(String itemId);

    public List<ItemsSpec> queryItemsSpecList(String itemId);

    public ItemsParam queryItemsParamById(String itemId);
}
