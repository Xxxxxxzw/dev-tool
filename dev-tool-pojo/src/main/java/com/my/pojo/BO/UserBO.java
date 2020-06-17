package com.my.pojo.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author xzw
 * @Date 2020/6/17
 */
@Data
@ApiModel(value = "用户注册的对象",description = "用户注册页的属性")
public class UserBO {
    @ApiModelProperty(value = "用户名",name="username",required = true,position = 1)
    private String username;
    @ApiModelProperty(value = "密码",name="password",required = true,position = 2)
    private String password;
    @ApiModelProperty(value = "确认密码",name="confirmPassword",required = true,position = 3)
    private String confirmPassword;
}
