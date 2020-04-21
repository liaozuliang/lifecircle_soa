package com.idianyou.lifecircle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath:spring-context.xml"})
public class LifecircleApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifecircleApiApplication.class, args);
    }

}