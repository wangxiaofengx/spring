package com.boot.service;

import com.boot.mapper.UserMapper;
import com.boot.po.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Resource
    UserMapper userMapper;

    public List<User> list() {
        logger.info("--------------------------------------------------------");
        throw new RuntimeException("开始抛异常");
        //return userMapper.list();
    }
}
