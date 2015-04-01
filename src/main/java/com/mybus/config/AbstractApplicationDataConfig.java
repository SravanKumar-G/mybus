package com.mybus.config;

import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.data.auditing.IsNewAwareAuditingHandler;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mapping.context.MappingContextIsNewStrategyFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.event.AuditingEventListener;
import org.springframework.data.support.IsNewStrategyFactory;

import static java.lang.String.format;

//import static org.springframework.util.StringUtils.isEmpty;

public abstract class AbstractApplicationDataConfig extends AbstractMongoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(AbstractApplicationDataConfig.class);

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
        String hostProp = getMongoSystemProperties().getProperty("mongo.host");
        String portProp = getMongoSystemProperties().getProperty("mongo.port");

        if (hostProp == null || hostProp == "") {
            throw new IllegalStateException("No mongo.host value was specified.");
        }
        if (portProp  == null || hostProp == "") {
            throw new IllegalStateException("No mongo.port value was specified.");
        }
        int port = Integer.valueOf(portProp);
        logger.info(format("Configuring mongo connection to host: '%s', port: %d", hostProp, port));
        Mongo mongo = new Mongo(hostProp, port);
        mongo.setWriteConcern(WriteConcern.SAFE);

        return mongo;
    }

    @Override
    protected UserCredentials getUserCredentials() {
        final String username = getMongoSystemProperties().getProperty("mongo.user");
        final String password = getMongoSystemProperties().getProperty("mongo.password");
        UserCredentials userCredentials = UserCredentials.NO_CREDENTIALS;
        if (username !=null || password!= null) {
            userCredentials = new UserCredentials(username, password);
        }
        return userCredentials;
    }

    @Bean
    public AuditingEventListener auditingEventListener() throws ClassNotFoundException {
        MongoMappingContext context = this.mongoMappingContext();
        IsNewStrategyFactory factory = new MappingContextIsNewStrategyFactory(context);
        IsNewAwareAuditingHandler<Object> handler = new IsNewAwareAuditingHandler<>(factory);
        return new AuditingEventListener(handler);
    }

    
   /* @Autowired
    private List<Converter<?, ?>> converters;

    @Override
    public CustomConversions customConversions() {
        return new CustomConversions(converters);
    }*/

}