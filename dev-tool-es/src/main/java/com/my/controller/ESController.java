package com.my.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author xzw
 * @Date 2021/3/8
 */
@RestController
public class ESController {

    @GetMapping("hello")
    public String hello(){
        return "hello elasticsearch!";
    }
}
