package com;

import com.common.config.CustomBanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Kouzhao {

    @Autowired
    RestTemplate restTemplate;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Kouzhao.class);
        app.setBanner(new CustomBanner());
        app.run(args);
    }




}
