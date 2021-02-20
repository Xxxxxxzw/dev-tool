package com.my.controller;

import com.my.pojo.Users;
import com.my.pojo.vo.UsersVO;
import com.my.service.UserService;
import com.my.utils.JSONResult;
import com.my.utils.JsonUtils;
import com.my.utils.MD5Utils;
import com.my.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @Author xzw
 * @Date 2020/6/16
 */
@Controller
public class SSOController {

    @Autowired
    UserService userService;
    @Autowired
    RedisOperator redisOperator;

    private static final String REDIS_USER_TOKEN = "redis_user_token";
    private static final String REDIS_USER_TICKET = "redis_user_ticket";
    private static final String REDIS_TMP_TICKET = "redis_tmp_ticket";
    private static final String COOKIE_USER_TICKET = "cookie_user_ticket";

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
        //1.获取userTicket门票，如果cookie中能够获取到，证明用户登录过，此时签发有效
        String userTicket = getCookie(request,COOKIE_USER_TICKET);
        boolean isVerified = isVerifyUserTicket(userTicket);
        if(isVerified){
            String tmpTicket = createTmpTicket();
            return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
        }
        //2.用户从未登录过
        return "login";
    }

    @PostMapping("/logout")
    @ResponseBody
    public JSONResult logout(String userId,
                        HttpServletResponse response,
                        HttpServletRequest request){
        //0.获取CAS中的用户门票
        String userTicket = getCookie(request,COOKIE_USER_TICKET);

        //1.清除userTicket票据，redis/cookie
        deleteCookie(COOKIE_USER_TICKET,response);
        redisOperator.del(REDIS_USER_TICKET + ":" + userTicket);

        //2.清除用户全局会话
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);
        return JSONResult.ok();
    }

    @GetMapping("/doLogin")
    public String doLogin(String username,
                        String password,
                        Model model,
                        String returnUrl,
                        HttpServletResponse response,
                        HttpServletRequest request) throws Exception{
        //TODO 后续完善是否登录
        //1.实现登录
        model.addAttribute("returnUrl",returnUrl);
        Users users = userService.queryUserLogin(username,MD5Utils.getMD5Str(password));
        if(users == null){
            model.addAttribute("errmsg","用户名密码不正确");
            return "login";
        }
        String uniqueToken = UUID.randomUUID().toString().trim();
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(users,usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(REDIS_USER_TOKEN + ":" + users.getId(),JsonUtils.objectToJson(usersVO));

        //3.生成ticket门票，全局门票，代表用户在CAS端登录过 -> userTicket ;
        String userTicket = UUID.randomUUID().toString().trim();

        //3.1用户全局门票需要放入CAS端的COOKIE中
        setCookie(COOKIE_USER_TICKET,userTicket,response);

        //4.userTicket关联用户id，并且放入到redis中，代表这个用户有门票了，可以在各个景区游玩了。
        redisOperator.set(REDIS_USER_TICKET + ":" + userTicket,users.getId());

        //5.生成临时票据，回调到调用端网站，是由CAS端所签发的一个一次性的临时ticket -> tmpTicket
        String tmpTicket = createTmpTicket();

        return "redirect:"+ returnUrl + "?tmpTicket=" + tmpTicket;
    }

    @PostMapping("/verifyTmpTicket")
    @ResponseBody
    public JSONResult verifyTmpTicket(String tmpTicket,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws Exception{
        String tmpTicketValue = redisOperator.get(REDIS_TMP_TICKET + ":" + tmpTicket);
        if(StringUtils.isBlank(tmpTicketValue)){
            return JSONResult.errorUserTicket("用户票据异常");
        }
        if(tmpTicketValue.equals(MD5Utils.getMD5Str(tmpTicket))){
            return JSONResult.errorUserTicket("用户票据异常");
        }else{
            //销毁临时票据
            redisOperator.del(REDIS_TMP_TICKET + ":" + tmpTicket);
        }

        //1.验证并且获取用户的userTicket
        String userTicket = getCookie(request,COOKIE_USER_TICKET);
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if(StringUtils.isBlank(userId)){
            return JSONResult.errorUserTicket("用户票据异常");
        }
        //2.验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if(StringUtils.isBlank(userRedis)){
            return JSONResult.errorUserTicket("用户票据异常");
        }

        UsersVO usersVO = JsonUtils.jsonToPojo(userRedis,UsersVO.class);
        return JSONResult.ok(usersVO);
    }
    /**
     * 创建临时票据
     * @return
     */
    private String createTmpTicket(){
       String tmpTicket = java.util.UUID.randomUUID().toString().trim();
       try{
           redisOperator.set(REDIS_TMP_TICKET + ":" + tmpTicket, MD5Utils.getMD5Str(tmpTicket));
       }catch (Exception e){
           e.printStackTrace();
       }
       return tmpTicket;
    }

    private void setCookie(String key,String val,HttpServletResponse response){
        Cookie cookie = new Cookie(key,val);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private String getCookie(HttpServletRequest request,String key){
        Cookie[] cookieList = request.getCookies();
        if(cookieList == null || StringUtils.isBlank(key)){
            return null;
        }
        String cookieValue = null;
        for(int i = 0 ; i < cookieList.length; i++){
            if(cookieList[i].getName().equals(key)){
                cookieValue = cookieList[i].getValue();
                break;
            }
        }
        return cookieValue;
    }

    private void deleteCookie(String key,HttpServletResponse response){
        Cookie cookie = new Cookie(key,null);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private boolean isVerifyUserTicket(String userTicket){
        //0.验证CAS门票不允许为空
        if(StringUtils.isBlank(userTicket)){
            return false;
        }
        //1.验证CAS门票是否有效
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if(StringUtils.isBlank(userId)){
            return false;
        }

        //2.验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if(StringUtils.isBlank(userRedis)){
            return false;
        }

        return true;
    }
}
