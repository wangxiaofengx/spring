package com;

import com.mrwang.config.CustomBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.setBanner(new CustomBanner());
//        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
}