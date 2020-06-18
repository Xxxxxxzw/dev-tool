package com.my.controller;

import com.my.enums.YesOrNo;
import com.my.pojo.Carousel;
import com.my.pojo.Category;
import com.my.pojo.vo.CategoryVO;
import com.my.service.CarouselService;
import com.my.service.CategoryService;
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
@Api(value = "首页",tags = {"用于首页内容展示"})
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    CarouselService carouselService;
    @Autowired
    CategoryService categoryService;

    @ApiOperation(value = "获取首页轮播图",notes = "用于获取首页轮播图",httpMethod = "GET")
    @GetMapping("/carousel")
    public JSONResult carousel(){
        List<Carousel> list = carouselService.queryAll(YesOrNo.YES.type);
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
        List<Category> list = categoryService.queryAllRootLevelCat();
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

        List<CategoryVO> list = categoryService.getSubCatList(rootCatId);
        return JSONResult.ok(list);
    }

}
