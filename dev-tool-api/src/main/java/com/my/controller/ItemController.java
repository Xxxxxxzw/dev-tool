package com.my.controller;

import com.my.enums.YesOrNo;
import com.my.pojo.*;
import com.my.pojo.vo.*;
import com.my.service.CarouselService;
import com.my.service.CategoryService;
import com.my.service.ItemService;
import com.my.utils.JSONResult;
import com.my.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author xzw
 * @Date 2020/6/18
 */
@Api(value = "商品",tags = {"用于商品内容展示"})
@RestController
@RequestMapping("items")
public class ItemController {

    @Autowired
    ItemService itemService;

    @ApiOperation(value = "查询商品详情", notes = "查询商品详情", httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public JSONResult subCat(
            @ApiParam(name = "itemId",value = "商品id", required = true)
            @PathVariable String itemId){
        if(itemId == null){
            return JSONResult.errorMsg("未传入商品id");
        }

        Items items = itemService.queryItemById(itemId);
        List<ItemsImg> itemsImgs = itemService.queryItemsImgList(itemId);
        List<ItemsSpec> itemsSpecs =  itemService.queryItemsSpecList(itemId);
        ItemsParam itemsParam = itemService.queryItemsParamById(itemId);

        ItemInfoVO itemInfoVO = new ItemInfoVO();
        itemInfoVO.setItem(items);
        itemInfoVO.setItemImgList(itemsImgs);
        itemInfoVO.setItemSpecList(itemsSpecs);
        itemInfoVO.setItemParams(itemsParam);

        return JSONResult.ok(itemInfoVO);
    }

    @ApiOperation(value = "查询商品评价等级", notes = "查询商品评价等级", httpMethod = "GET")
    @GetMapping("/commentLevel")
    public JSONResult commentLevel(
            @ApiParam(name = "itemId",value = "商品id", required = true)
            @RequestParam String itemId){
        if(itemId == null){
            return JSONResult.errorMsg("未传入商品id");
        }
        CommentLevelCountsVO commentLevelCountsVO = itemService.queryCommentCounts(itemId);

        return JSONResult.ok(commentLevelCountsVO);
    }

    @ApiOperation(value = "查询商品评价等级", notes = "查询商品评价等级", httpMethod = "GET")
    @GetMapping("/comments")
    public JSONResult comments(
            @ApiParam(name = "itemId",value = "商品id", required = true)
            @RequestParam String itemId,
            @ApiParam(name = "level",value = "等级", required = true)
            @RequestParam Integer level,
            @ApiParam(name = "page",value = "页码", required = true)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize",value = "页数量", required = true)
            @RequestParam Integer pageSize){
        if(itemId == null){
            return JSONResult.errorMsg("未传入商品id");
        }
        if(page == null){
            page = 1;
        }
        if(pageSize == null){
            pageSize = 10;
        }

        PagedGridResult grid = itemService.queryPagedComments(itemId,level,page,pageSize);
        return JSONResult.ok(grid);
    }

    @ApiOperation(value = "搜索商品列表", notes = "搜索商品列表", httpMethod = "GET")
    @GetMapping("/search")
    public JSONResult search(
            @ApiParam(name = "keywords", value = "关键字", required = true)
            @RequestParam String keywords,
            @ApiParam(name = "sort", value = "排序", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            return JSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PagedGridResult grid = itemService.searhItems(keywords,
                sort,
                page,
                pageSize);

        return JSONResult.ok(grid);
    }

    @ApiOperation(value = "通过分类id搜索商品列表", notes = "通过分类id搜索商品列表", httpMethod = "GET")
    @GetMapping("/catItems")
    public JSONResult catItems(
            @ApiParam(name = "catId", value = "三级分类id", required = true)
            @RequestParam Integer catId,
            @ApiParam(name = "sort", value = "排序", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (catId == null) {
            return JSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PagedGridResult grid = itemService.searhItems(catId,
                sort,
                page,
                pageSize);

        return JSONResult.ok(grid);
    }

    //用于长时间用户未登录，刷新购物车中的数据（主要是商品价格）
    @ApiOperation(value = "根据商品规格ids查找商品数据", notes = "根据商品规格ids查找商品数据", httpMethod = "GET")
    @GetMapping("/refresh")
    public JSONResult refresh(
            @ApiParam(name = "itemSpecIds", value = "商品规格ids", required = true ,example = "1001,1002,1003")
            @RequestParam String itemSpecIds) {

        if(StringUtils.isBlank(itemSpecIds)){
            return JSONResult.ok();
        }
        List<ShopcartVO> list = itemService.queryItemsBySpecIds(itemSpecIds);
        return JSONResult.ok(list);
    }

}
