package com.my.controller;

import com.my.pojo.BO.ShopcartBO;
import com.my.pojo.BO.UserBO;
import com.my.pojo.Users;
import com.my.pojo.vo.UsersVO;
import com.my.service.UserService;
import com.my.utils.*;
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

import java.util.ArrayList;
import java.util.List;

import static com.my.controller.BaseController.FOODIE_SHOPCART;

/**
 * @Author xzw
 * @Date 2020/6/17
 */
@Api(value = "用户注册登录" ,tags = {"用于用户注册登录的接口"})
@RestController
@RequestMapping("passport")
public class PassportController  extends BaseController {

    public static Logger logger = LoggerFactory.getLogger(PassportController.class);

    @Autowired
    private RedisOperator redisOperator;
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
    public JSONResult register(@RequestBody UserBO userBO,
                               HttpServletRequest request,
                               HttpServletResponse response){
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

        Users users = userService.createUser(userBO);

        // TODO 生成用户token，存入redis会话
        // 实现用户的redis会话
        UsersVO usersVO = conventUsersVO(users);

        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);
        // 同步购物车数据
        synchShopcartData(users.getId(), request, response);
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
        // TODO 生成用户token，存入redis会话
        // 实现用户的redis会话
        UsersVO usersVO = conventUsersVO(users);

        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);

        // 同步购物车数据
        synchShopcartData(users.getId(), request, response);
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

    /**
     * 注册登录成功后，同步cookie和redis中的购物车数据
     */
    private void synchShopcartData(String userId, HttpServletRequest request,
                                   HttpServletResponse response) {

        /**
         * 1. redis中无数据，如果cookie中的购物车为空，那么这个时候不做任何处理
         *                 如果cookie中的购物车不为空，此时直接放入redis中
         * 2. redis中有数据，如果cookie中的购物车为空，那么直接把redis的购物车覆盖本地cookie
         *                 如果cookie中的购物车不为空，
         *                      如果cookie中的某个商品在redis中存在，
         *                      则以cookie为主，删除redis中的，
         *                      把cookie中的商品直接覆盖redis中（参考京东）
         * 3. 同步到redis中去了以后，覆盖本地cookie购物车的数据，保证本地购物车的数据是同步最新的
         */

        // 从redis中获取购物车
        String shopcartJsonRedis = redisOperator.get(FOODIE_SHOPCART + ":" + userId);

        // 从cookie中获取购物车
        String shopcartStrCookie = CookieUtils.getCookieValue(request, FOODIE_SHOPCART, true);

        if (StringUtils.isBlank(shopcartJsonRedis)) {
            // redis为空，cookie不为空，直接把cookie中的数据放入redis
            if (StringUtils.isNotBlank(shopcartStrCookie)) {
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, shopcartStrCookie);
            }
        } else {
            // redis不为空，cookie不为空，合并cookie和redis中购物车的商品数据（同一商品则覆盖redis）
            if (StringUtils.isNotBlank(shopcartStrCookie)) {

                /**
                 * 1. 已经存在的，把cookie中对应的数量，覆盖redis（参考京东）
                 * 2. 该项商品标记为待删除，统一放入一个待删除的list
                 * 3. 从cookie中清理所有的待删除list
                 * 4. 合并redis和cookie中的数据
                 * 5. 更新到redis和cookie中
                 */

                List<ShopcartBO> shopcartListRedis = JsonUtils.jsonToList(shopcartJsonRedis, ShopcartBO.class);
                List<ShopcartBO> shopcartListCookie = JsonUtils.jsonToList(shopcartStrCookie, ShopcartBO.class);

                // 定义一个待删除list
                List<ShopcartBO> pendingDeleteList = new ArrayList<>();

                for (ShopcartBO redisShopcart : shopcartListRedis) {
                    String redisSpecId = redisShopcart.getSpecId();

                    for (ShopcartBO cookieShopcart : shopcartListCookie) {
                        String cookieSpecId = cookieShopcart.getSpecId();

                        if (redisSpecId.equals(cookieSpecId)) {
                            // 覆盖购买数量，不累加，参考京东
                            redisShopcart.setBuyCounts(cookieShopcart.getBuyCounts());
                            // 把cookieShopcart放入待删除列表，用于最后的删除与合并
                            pendingDeleteList.add(cookieShopcart);
                        }

                    }
                }

                // 从现有cookie中删除对应的覆盖过的商品数据
                shopcartListCookie.removeAll(pendingDeleteList);

                // 合并两个list
                shopcartListRedis.addAll(shopcartListCookie);
                // 更新到redis和cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopcartListRedis), true);
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopcartListRedis));
            } else {
                // redis不为空，cookie为空，直接把redis覆盖cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, shopcartJsonRedis, true);
            }

        }
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
