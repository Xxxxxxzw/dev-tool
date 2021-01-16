package com.my.controller;

import com.my.pojo.BO.UserBO;
import com.my.pojo.Users;
import com.my.service.UserService;
import com.my.utils.CookieUtils;
import com.my.utils.JSONResult;
import com.my.utils.JsonUtils;
import com.my.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author xzw
 * @Date 2020/6/17
 */
@Api(value = "用户注册登录" ,tags = {"用于用户注册登录的接口"})
@RestController
@RequestMapping("passport")
public class PassportController {

    public static Logger logger = LoggerFactory.getLogger(PassportController.class);

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
    @PostMapping("regist")
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

    @ApiOperation(value = "用户登录",notes = "用于用户登录",httpMethod = "POST")
    @PostMapping("login")
    public JSONResult login(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) throws Exception{
        String username = userBO.getUsername();
        String password = userBO.getPassword();

        //0.判断用户名密码是否为空
        if(StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)){
            return JSONResult.errorMsg("用户名或密码为空");
        }

        Users users = userService.queryUserLogin(username, MD5Utils.getMD5Str(password));
        if(users == null){
            return JSONResult.errorMsg("用户名或密码不正确");
        }

        users = setNullProperty(users);

        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(users),true);
        return JSONResult.ok(users);
    }

    @ApiOperation(value = "用户退出登录",notes = "用于用户退出登录",httpMethod = "POST")
    @PostMapping("logout")
    public JSONResult login(@RequestBody String userId,
                            HttpServletRequest request,
                            HttpServletResponse response) throws Exception{
        CookieUtils.deleteCookie(request,response,"user");

        return JSONResult.ok(null);
    }

    private Users setNullProperty(Users users){
        users.setBirthday(null);
        users.setCreatedTime(null);
        users.setEmail(null);
        users.setPassword(null);
        users.setUpdatedTime(null);
        users.setMobile(null);
        return users;
    }
}
