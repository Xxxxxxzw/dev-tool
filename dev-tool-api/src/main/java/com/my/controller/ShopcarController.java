package com.my.controller;

import com.my.pojo.BO.ShopcartBO;
import com.my.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author xzw
 * @Date 2020/7/2
 */
@Api(value = "购物车",tags = {"用于购物车内容展示"})
@RestController
@RequestMapping("shopcart")
public class ShopcarController {

    @ApiOperation(value = "添加商品至购物车", notes = "添加商品至购物车", httpMethod = "POST")
    @PostMapping("add")
    public JSONResult add(@RequestParam String userId,
                          @RequestBody ShopcartBO shopcartBO,
                          HttpServletResponse response,
                          HttpServletRequest request){

        if(StringUtils.isBlank(userId)){
            return JSONResult.errorMsg("未传入userId");
        }
        return JSONResult.ok();
    }

    @ApiOperation(value = "删除购物车中商品", notes = "删除购物车中商品", httpMethod = "POST")
    @PostMapping("del")
    public JSONResult del(@RequestParam String userId,
                          @RequestParam String itemSpecId,
                          HttpServletResponse response,
                          HttpServletRequest request){

        if(StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)){
            return JSONResult.errorMsg("参数未传入");
        }
        return JSONResult.ok();
    }
}
