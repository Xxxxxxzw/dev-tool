package com.my.controller;

import com.my.enums.YesOrNo;
import com.my.pojo.*;
import com.my.pojo.vo.CategoryVO;
import com.my.pojo.vo.ItemInfoVO;
import com.my.pojo.vo.NewItemsVO;
import com.my.service.CarouselService;
import com.my.service.CategoryService;
import com.my.service.ItemService;
import com.my.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author xzw
 * @Date 2020/6/18
 */
@Api(value = "商品",tags = {"用于商品内容展示"})
@RestController
@RequestMapping("item")
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

}
