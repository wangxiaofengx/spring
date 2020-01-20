package com.manager.controller;

import com.manager.po.Bank;
import com.manager.repository.BankRepository;
import com.manager.repository.MapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/bank")
public class BankController {

    @Autowired
    BankRepository bankRepository;

    @Autowired
    MapRepository mapRepository;


    @RequestMapping("/insert")
    public Bank insert() {
        Bank bank = new Bank();
        bank.setAccountNumber(1l);
        bank.setAddress("aaa");
        bankRepository.save(bank);
        return bank;
    }

    @RequestMapping("/insertMap")
    public Map insertMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("a", "123");
        map.put("b", "456");
        List data = new ArrayList();
        Map<String, String> a = new HashMap<>();
        a.put("name", "zhangsan");
        data.add(a);
        map.put("child", data);
        mapRepository.save(map);
        return map;
    }

    @RequestMapping("/get/Map")
    public List getMap() {
        return mapRepository.findAll();
    }

    @RequestMapping("/getAll")
    public List getAll() {
        return mapRepository.findAll();
    }
}
