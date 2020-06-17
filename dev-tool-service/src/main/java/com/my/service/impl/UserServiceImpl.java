package com.my.service.impl;

import com.my.enums.Sex;
import com.my.mapper.UsersMapper;
import com.my.pojo.BO.UserBO;
import com.my.pojo.Users;
import com.my.service.UserService;
import com.my.utils.DateUtil;
import com.my.utils.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @Author xzw
 * @Date 2020/6/17
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();

        userCriteria.andEqualTo("username",username);

        Users users = usersMapper.selectOneByExample(userExample);

        return users == null ?false:true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users createUser(UserBO userBO) {

        String userId = sid.nextShort();
        Users users = new Users();
        users.setUsername(userBO.getUsername());
        try {
            users.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        users.setNickname(userBO.getUsername());
        users.setSex(Sex.secret.type);
        users.setBirthday(DateUtil.stringToDate("1900-01-01"));
        users.setId(userId);
        users.setCreatedTime(new Date());
        users.setUpdatedTime(new Date());
        users.setFace("http://122.152.205.72:88/group1/M00/00/05/CpoxxFw_8_qAIlFXAAAcIhVPdSg994.png");
        usersMapper.insert(users);
        return users;
    }
}
