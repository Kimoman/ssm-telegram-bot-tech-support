package com.study.botssm.botssmstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class BotssmstudyApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(BotssmstudyApplication.class, args);
    }

}
