package com.mrwang.service;

import com.mrwang.mapper.UserMapper;
import com.mrwang.po.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    @Resource
    UserMapper userMapper;

    public List<User> list() {

        return userMapper.list();
    }
}
