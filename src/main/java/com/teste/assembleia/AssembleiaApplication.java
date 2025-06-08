package com.teste.assembleia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AssembleiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssembleiaApplication.class, args);
    }

}
