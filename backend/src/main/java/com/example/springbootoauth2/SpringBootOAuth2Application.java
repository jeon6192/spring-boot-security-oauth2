package com.example.springbootoauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@EnableConfigurationProperties
@ConfigurationPropertiesScan
@EntityScan(basePackages = { "com.example.springbootoauth2.model.entity" })
@EnableJpaRepositories(basePackages = { "com.example.springbootoauth2.repository" })
@SpringBootApplication
public class SpringBootOAuth2Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootOAuth2Application.class, args);
    }

}
