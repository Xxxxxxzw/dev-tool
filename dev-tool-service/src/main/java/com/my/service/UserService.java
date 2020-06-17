package com.my.service;

import com.my.pojo.BO.UserBO;
import com.my.pojo.Users;

/**
 * @Author xzw
 * @Date 2020/6/17
 */
public interface UserService {
    public boolean queryUsernameIsExist(String username);

    public Users createUser(UserBO userBO);
}
