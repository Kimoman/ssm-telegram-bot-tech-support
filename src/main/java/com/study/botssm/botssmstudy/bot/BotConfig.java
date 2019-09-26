package com.study.botssm.botssmstudy.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:app.properties")
public class BotConfig {

    @Value("${bot.token}")
    private String token;

    @Value("${bot.name}")
    private String username;

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }
}
