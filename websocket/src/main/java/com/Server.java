package com;

import com.common.config.CustomBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
public class Server {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Server.class);
        app.setBanner(new CustomBanner());
        app.run(args);
    }
}