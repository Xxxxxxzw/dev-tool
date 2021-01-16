package com.my.controller;

import com.my.enums.YesOrNo;
import com.my.pojo.Carousel;
import com.my.pojo.Category;
import com.my.pojo.vo.CategoryVO;
import com.my.pojo.vo.NewItemsVO;
import com.my.service.CarouselService;
import com.my.service.CategoryService;
import com.my.utils.JSONResult;
import com.my.utils.JsonUtils;
import com.my.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author xzw
 * @Date 2020/6/18
 */
@Api(value = "首页",tags = {"用于首页内容展示"})
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    CarouselService carouselService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    RedisOperator redisOperator;

    @ApiOperation(value = "获取首页轮播图",notes = "用于获取首页轮播图",httpMethod = "GET")
    @GetMapping("/carousel")
    public JSONResult carousel(){
        String carousel = redisOperator.get("carousel");
        List<Carousel> list = new ArrayList<>();
        if(StringUtils.isNotBlank(carousel)){
            list = JsonUtils.jsonToList(carousel,Carousel.class);
        }else{
            list = carouselService.queryAll(YesOrNo.YES.type);
            redisOperator.set("carousel",JsonUtils.objectToJson(list));
        }

        return JSONResult.ok(list);
    }

    /**
     * 首页分类展示需求：
     * 1. 第一次刷新主页查询大分类，渲染展示到首页
     * 2. 如果鼠标上移到大分类，则加载其子分类的内容，如果已经存在子分类，则不需要加载（懒加载）
     */
    @ApiOperation(value = "获取商品分类(一级分类)",notes = "获取商品分类(一级分类)",httpMethod = "GET")
    @GetMapping("/cats")
    public JSONResult cats(){
        List<Category> list = new ArrayList<>();
        String catsStr = redisOperator.get("cats");
        if (StringUtils.isBlank(catsStr)) {
            list = categoryService.queryAllRootLevelCat();
            redisOperator.set("cats", JsonUtils.objectToJson(list));
        } else {
            list = JsonUtils.jsonToList(catsStr, Category.class);
        }
        return JSONResult.ok(list);
    }

    @ApiOperation(value = "获取商品子分类", notes = "获取商品子分类", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public JSONResult subCat(
            @ApiParam(name = "rootCatId",value = "一级分类id", required = true)
            @PathVariable Integer rootCatId){
        if(rootCatId == null){
            return JSONResult.errorMsg("未传入一级分类id");
        }
        List<CategoryVO> list = new ArrayList<>();
        String catsStr = redisOperator.get("subCat:" + rootCatId);
        if (StringUtils.isBlank(catsStr)) {
            list = categoryService.getSubCatList(rootCatId);

            /**
             * 查询的key在redis中不存在，
             * 对应的id在数据库也不存在，
             * 此时被非法用户进行攻击，大量的请求会直接打在db上，
             * 造成宕机，从而影响整个系统，
             * 这种现象称之为缓存穿透。
             * 解决方案：把空的数据也缓存起来，比如空字符串，空对象，空数组或list
             */
            if (list != null && list.size() > 0) {
                redisOperator.set("subCat:" + rootCatId, JsonUtils.objectToJson(list));
            } else {
                redisOperator.set("subCat:" + rootCatId, JsonUtils.objectToJson(list), 5*60);
            }
        } else {
            list = JsonUtils.jsonToList(catsStr, CategoryVO.class);
        }
        return JSONResult.ok(list);
    }

    @ApiOperation(value = "获取每个一级分类下的最新6个商品", notes = "获取每个一级分类下的最新6个商品", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public JSONResult sixNewItems(
            @ApiParam(name = "rootCatId",value = "一级分类id", required = true)
            @PathVariable Integer rootCatId){
        if(rootCatId == null){
            return JSONResult.errorMsg("未传入一级分类id");
        }
        List<NewItemsVO> list = categoryService.getSixNewItemsLazy(rootCatId);
        return JSONResult.ok(list);
    }
}
