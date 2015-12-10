package com.mybus.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@Profile("test")
@ComponentScan(basePackages = "com.mybus")
@PropertySource(name = "mongoProperties", value = "classpath:test-mongo-config.properties")
@EnableMongoRepositories(basePackages = "com.mybus")
@EnableMongoAuditing
public class ApplicationDataTestConfig extends AbstractApplicationDataConfig {

    private static final String HOME_DIR_PROPS_FILENAME = ".mybus.mongo.test.properties";

    @Autowired
    private Environment mongoProperties;

    @Bean
    @Override
    MongoSystemProperties getMongoSystemProperties() {
        return new MongoSystemProperties(mongoProperties, getHomeDirPropertiesFilename());
    }

    @Override
    String getHomeDirPropertiesFilename() {
        return HOME_DIR_PROPS_FILENAME;
    }
}
