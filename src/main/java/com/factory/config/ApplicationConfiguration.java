package com.factory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {
    @Bean
    @ConfigurationProperties("app.config")
    public AppConfig appConfig(){
        return new AppConfig();
    }
}
