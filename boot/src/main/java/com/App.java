package com;

import com.zy.config.CustomBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan("com.zy")
public class App {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.setBanner(new CustomBanner());
//        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }
}