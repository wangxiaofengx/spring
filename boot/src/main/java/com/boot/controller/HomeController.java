package com.boot.controller;

import com.boot.po.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpSession;

@RestController
public class HomeController {

    @RequestMapping("/list")
    public String list(HttpSession session) {
        return session.getId();
    }

    @GetMapping("/user")
    public Mono<User> getUser() {
        User user = new User();
        user.setUsername("张三");
        user.setPassword("123456");
        return Mono.just(user);
    }
}
