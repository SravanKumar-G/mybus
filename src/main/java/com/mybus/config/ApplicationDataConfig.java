package com.mybus.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableScheduling
@Profile("default")
@ComponentScan(basePackages = "com.mybus")
@PropertySource(name = "mongoProperties", value = "classpath:mongo-config.properties")
@EnableMongoRepositories(basePackages = "com.mybus.dao")
public class ApplicationDataConfig extends AbstractApplicationDataConfig {

	private static final String HOME_DIR_PROPS_FILENAME = ".mybus.mongo.properties";
	private static final Logger logger = LoggerFactory.getLogger(ApplicationDataConfig.class);
	
    @Autowired
    private Environment mongoProperties;
    
    @Autowired 
    private CoreAppConfig config;

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
