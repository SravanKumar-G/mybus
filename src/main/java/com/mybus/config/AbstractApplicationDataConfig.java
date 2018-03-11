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
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

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
                    Collections.singletonList(MongoCredential.createCredential("","mybus", null)));
        }
        return null;
        /*logger.info("Configuring mongo connection...");

        for (int i = 0; i < MAX_NODES_IN_REPLICA_SET; i++) {
            String hostPropertyName = format("mongo.%d.host", i);
            String host = getMongoSystemProperties().getProperty(hostPropertyName);
            String portPropertyName = format("mongo.%d.port", i);
            String portStr = getMongoSystemProperties().getProperty(portPropertyName);
            if (StringUtils.isBlank(host) || StringUtils.isBlank(portStr)) {
                break;
            }
            int port = toInt(portStr);
            Assert.isTrue(port > 0, format("Invalid value '%s' for property '%s'", portStr, portPropertyName));
            servers.add(new ServerAddress(host, port));
            logger.info(format("Adding connection to mongo server [%d]: host: '%s', port: %d", i, host, port));
        }

        Assert.notEmpty(servers, "No mongo hosts were defined in the mongo properties file.");
        logger.info(format("A total of %d mongo servers have been configured.", servers.size()));
        Mongo mongo = new Mongo(servers);
        mongo.setWriteConcern(WriteConcern.SAFE);

        return mongo;*/
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
