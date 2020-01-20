package com.manager.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class HomeController {

    @RequestMapping("/list")
    public String list(HttpSession session) {
        return session.getId();
    }


}
