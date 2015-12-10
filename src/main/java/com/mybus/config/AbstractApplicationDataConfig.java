package com.mybus.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.lang.math.NumberUtils.toInt;

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
    protected String getMappingBasePackage() {
        return "com.shodogg.ube";
    }

    @Override
    public final Mongo mongo() throws Exception {
        List<ServerAddress> servers = new ArrayList<>(MAX_NODES_IN_REPLICA_SET);
        String hostProp = getMongoSystemProperties().getProperty("mongo.host");
        String portProp = getMongoSystemProperties().getProperty("mongo.port");
        if (!StringUtils.isBlank(hostProp) || StringUtils.isBlank(portProp)) {
            servers.add(new ServerAddress(hostProp, Integer.valueOf(portProp)));
        }
        logger.info("Configuring mongo connection...");

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

        return mongo;
    }


    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }
}
