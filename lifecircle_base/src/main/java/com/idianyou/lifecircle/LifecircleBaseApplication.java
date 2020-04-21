package com.idianyou.lifecircle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath:spring-context.xml"})
public class LifecircleBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifecircleBaseApplication.class, args);
    }

}