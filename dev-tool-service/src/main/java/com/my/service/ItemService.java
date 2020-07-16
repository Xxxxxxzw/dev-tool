package com.my.service;

import com.my.pojo.Items;
import com.my.pojo.ItemsImg;
import com.my.pojo.ItemsParam;
import com.my.pojo.ItemsSpec;
import com.my.pojo.vo.CommentLevelCountsVO;
import com.my.pojo.vo.ItemCommentVO;
import com.my.pojo.vo.ShopcartVO;
import com.my.utils.PagedGridResult;

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

    public CommentLevelCountsVO queryCommentCounts(String itemId);

    public PagedGridResult queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize);

    /**
     * 搜索商品列表
     * @param keywords
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searhItems(String keywords, String sort,
                                      Integer page, Integer pageSize);

    /**
     * 根据分类id搜索商品列表
     * @param catId
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searhItems(Integer catId, String sort,
                                      Integer page, Integer pageSize);

    public List<ShopcartVO> queryItemsBySpecIds(String specIds);

    /**
     * 根据商品规格id获取规格对象的具体信息
     * @param specId
     * @return
     */
    public ItemsSpec queryItemSpecById(String specId);

    /**
     * 根据商品id获得商品图片主图url
     * @param itemId
     * @return
     */
    public String queryItemMainImgById(String itemId);

    /**
     * 减少库存
     * @param specId
     * @param buyCounts
     */
    public void decreaseItemSpecStock(String specId, int buyCounts);
}
