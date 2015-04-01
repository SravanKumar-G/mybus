package com.mybus.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mybus.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan(basePackages = "com.mybus")
@PropertySource(name = "env", value = "classpath:app-config.properties")
public class CoreAppConfig {

    @Autowired
    private Environment env;

    @Bean
    public SystemProperties props() {
        return new SystemProperties(env);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }
        
}
