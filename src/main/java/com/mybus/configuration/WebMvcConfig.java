package com.mybus.configuration;

import com.mybus.interceptors.AuthenticationInterceptor;
import com.mybus.interceptors.DomainFilterInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration

@ComponentScan(basePackages = "com.mybus")
@EnableMongoRepositories(basePackages = "com.mybus")
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private DomainFilterInterceptor domainFilterInterceptor;

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(domainFilterInterceptor);
        registry.addInterceptor(authenticationInterceptor);
    }

}