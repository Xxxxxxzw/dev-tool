package com.my.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Author xzw
 * @Date 2020/6/16
 */
@RestController
@ApiIgnore
public class HelloController {

    @GetMapping("/hello")
    public Object hello(){
        return "hello";
    }
}
