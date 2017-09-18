package com.mybus.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mybus.SystemProperties;
import com.mybus.util.MyBusAWSCredentialsProvider;
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
        // for Hibernate 4.x:
       // mapper.registerModule(new Hibernate4Module());
// or, for Hibernate 5.x
        //mapper.registerModule(new Hibernate5Module());
// or, for Hibernate 3.6
        //mapper.registerModule(new Hibernate3Module());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }
    /**
     * Returns an {@link com.amazonaws.auth.AWSCredentialsProvider} with the permissions necessary to accomplish all
     * specified tasks. At the minimum it will require read permissions for Amazon Kinesis. Additional read permissions
     * and write permissions may be required based on the Pipeline used.
     *
     * @return
     */
    @Bean
    public AWSCredentialsProvider getAWSCredentialsProvider() {
        return new AWSCredentialsProviderChain(new MyBusAWSCredentialsProvider());
    }
}
