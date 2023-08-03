package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.util.InitAplication.initApplication;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        initApplication();
        SpringApplication.run(Application.class, args);
    }
}
