package com.my.controller;

import com.my.mapper.StuMapper;
import com.my.pojo.Stu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Author xzw
 * @Date 2020/6/17
 */
@ApiIgnore
@RestController
public class StuController {

    @Autowired
    StuMapper stuMapper;

    @GetMapping("/stu/get")
    public Object get(int id){
        Stu stu = stuMapper.selectByPrimaryKey(id);
        return stu;
    }
}
