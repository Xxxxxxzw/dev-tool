package com.my.controller;

import com.my.pojo.BO.UserBO;
import com.my.service.UserService;
import com.my.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Author xzw
 * @Date 2020/6/17
 */
@Api(value = "用户注册登录" ,tags = {"用于用户注册登录的接口"})
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    UserService userService;

    @ApiOperation(value = "查询用户名是否存在",notes = "用于查询用户是否存在",httpMethod = "GET")
    @GetMapping("usernameIsExist")
    public JSONResult usernameIsExist(@RequestParam String username){

        if(StringUtils.isBlank(username)){
            return JSONResult.errorMsg("用户名不能为空");
        }
        if(userService.queryUsernameIsExist(username)){
            return JSONResult.errorMsg("用户名已存在");
        }
        return JSONResult.ok();
    }

    @ApiOperation(value = "注册用户",notes = "用于注册用户",httpMethod = "POST")
    @PostMapping("register")
    public JSONResult register(@RequestBody UserBO userBO){
        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPassword = userBO.getConfirmPassword();

        //0.判断用户名密码是否为空
        if(StringUtils.isBlank(username) ||
                StringUtils.isBlank(password) ||
                StringUtils.isBlank(confirmPassword)){
            return JSONResult.errorMsg("用户名或密码为空");
        }
        //1.判断用户名是否存在
        if(userService.queryUsernameIsExist(username)){
            return JSONResult.errorMsg("用户名已存在");
        }
        //2.密码是否小于6位
        if(password.length()<6){
            return JSONResult.errorMsg("密码小于6位");
        }
        //3.密码是否一致
        if(!password.equals(confirmPassword)){
            return JSONResult.errorMsg("密码与确认密码不一致");
        }

        userService.createUser(userBO);
        return JSONResult.ok();
    }
}
