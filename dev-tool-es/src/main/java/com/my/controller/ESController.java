package com.my.controller;

import com.my.service.ItemsEsService;
import com.my.utils.JSONResult;
import com.my.utils.PagedGridResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author xzw
 * @Date 2021/3/8
 */
@RestController
@RequestMapping("items")
public class ESController {

    @Autowired
    ItemsEsService itemsEsService;

    @GetMapping("hello")
    public String hello(){
        return "hello elasticsearch!";
    }

    @GetMapping("/es/search")
    public JSONResult search(
            String keywords,
            String sort,
            Integer page,
            Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            return JSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }
        page--;
        if (pageSize == null) {
            pageSize = 10;
        }

        PagedGridResult grid = itemsEsService.searchItems(keywords,
                sort,
                page,
                pageSize);

        return JSONResult.ok(grid);
    }
}
