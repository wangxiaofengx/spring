package com.elk.controller;

import com.common.util.SnowFlake;
import com.elk.po.User;
import com.elk.service.UserService;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping("/list")
    public List<User> list() {
        return userService.list();
    }


    @RequestMapping("/insert")
    public User save() {
        User user = new User();
        user.setId(SnowFlake.nextId());
        user.setAge(RandomUtils.nextInt(12, 80));
        user.setUsername("zhangsan");
        user.setPassword("123456");
        return userService.insert(user);
    }
}
