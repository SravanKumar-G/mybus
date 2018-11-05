package com.mybus.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration

@ComponentScan(basePackages = "com.mybus")
@EnableMongoRepositories(basePackages = "com.mybus")
public class WebMvcConfig implements WebMvcConfigurer {


}