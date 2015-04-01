package com.mybus.config;

import com.mybus.util.PropertiesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.Properties;

public final class MongoSystemProperties {
 
    @Autowired
    private Environment env;

    private Properties homeDirProperties;

    private final String homeDirPropertiesFilename;

    public MongoSystemProperties(final Environment environment, final String homeDirPropertiesFilename) {
        if (environment == null) {
            throw new IllegalArgumentException("Environment cannot be null when initializing "
                    + this.getClass().getName());
        }
        this.homeDirPropertiesFilename = homeDirPropertiesFilename;
        this.env = environment;
        this.homeDirProperties = getPropertiesFromHomeDir();
    }

    public String getProperty(final String propertyName) {
        if (homeDirProperties != null) {
            if (homeDirProperties.containsKey(propertyName)) {
                return homeDirProperties.getProperty(propertyName);
            }
        }
        return env.getProperty(propertyName, "");
    }

    private Properties getPropertiesFromHomeDir() {
        return PropertiesUtils.getPropertiesFromHomeDirFile(homeDirPropertiesFilename);
    }
    
}
