package com.mybus.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
public abstract class AbstractApplicationDataConfig {
}
/*
public abstract class AbstractApplicationDataConfig extends AbstractMongoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(AbstractApplicationDataConfig.class);
    private static final int MAX_NODES_IN_REPLICA_SET = 50;

    abstract MongoSystemProperties getMongoSystemProperties();

    abstract String getHomeDirPropertiesFilename();

    @Override
    protected final String getDatabaseName() {
        final String dbName = getMongoSystemProperties().getProperty("mongo.db");
        logger.info(format("Using mongo database '%s'", dbName));
        return dbName;
    }

    @Override
    public MongoClient mongoClient() {
        List<ServerAddress> servers = new ArrayList<>(MAX_NODES_IN_REPLICA_SET);
        String hostProp = getMongoSystemProperties().getProperty("mongo.host");
        String portProp = getMongoSystemProperties().getProperty("mongo.port");
        if (!StringUtils.isBlank(hostProp) || StringUtils.isBlank(portProp)) {
            return new MongoClient(Collections.singletonList(new ServerAddress(hostProp, Integer.parseInt(portProp))),
                    Collections.singletonList(MongoCredential.createCredential(
                            getMongoSystemProperties().getProperty("mongo.user"),
                            getMongoSystemProperties().getProperty("mongo.db"),
                            getMongoSystemProperties().getProperty("mongo.password").toCharArray())));
        }
        return null;

    }


    //remove _class
    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory,
                                       MongoMappingContext context) {

        MappingMongoConverter converter =
                new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), context);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, converter);

        return mongoTemplate;

    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

}
*/
