package com.lifecycle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LifeCycleApplication {
    public static void main(String[] args) { SpringApplication.run(LifeCycleApplication.class, args); }
}
