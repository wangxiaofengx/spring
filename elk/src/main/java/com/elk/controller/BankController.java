package com.elk.controller;

import com.elk.po.Bank;
import com.elk.repository.BankRepository;
import com.elk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController()
@RequestMapping("/bank")
public class BankController {

    @Autowired
    BankRepository bankRepository;

    @Autowired
    UserService userService;

    @RequestMapping("/list")
    @Cacheable(value = "dhgl")
    public Page<Bank> list(HttpSession session) {
        session.setAttribute("a","a");
        Page<Bank> page = bankRepository.findAll(PageRequest.of(1, 100));
        return page;
    }

}
