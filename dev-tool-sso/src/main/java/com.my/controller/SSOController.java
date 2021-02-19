package com.my.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author xzw
 * @Date 2020/6/16
 */
@Controller
public class SSOController {

    @GetMapping("/hello")
    @ResponseBody
    public Object hello(){
        return "hello";
    }

    @GetMapping("/login")
    public String login(String returnUrl,
                        Model model,
                        HttpServletResponse response,
                        HttpServletRequest request){
        model.addAttribute("returnUrl",returnUrl);
        //TODO 后续完善是否登录
        return "login";
    }


    @GetMapping("/doLogin")
    public String doLogin(String username,
                        String password,
                        Model model,
                        String returnUrl,
                        HttpServletResponse response,
                        HttpServletRequest request){
        //TODO 后续完善是否登录
        return "returnUrl";
    }
}
